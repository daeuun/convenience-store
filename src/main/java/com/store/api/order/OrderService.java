package com.store.api.order;

import com.store.api.common.exception.ExceptionCode;
import com.store.api.order.model.FinalizedOrderDetailRequest;
import com.store.api.order.model.Order;
import com.store.api.order.model.OrderDetail;
import com.store.api.order.model.OrderItem;
import com.store.api.order.model.OrderItemResponse;
import com.store.api.order.model.OrderRequest;
import com.store.api.order.model.OrderResponse;
import com.store.api.order.model.OrderStatus;
import com.store.api.order.model.OrderStatusResponse;
import com.store.api.order.model.OrderValidateRequest;
import com.store.api.order.resource.OrderItemRepository;
import com.store.api.order.resource.OrderRepository;
import com.store.api.product.ProductService;
import com.store.api.product.model.Product;
import com.store.api.product.resource.ProductRepository;
import com.store.api.product.saleproduct.model.SaleProduct;
import com.store.api.product.saleproduct.resource.SaleProductRepository;
import com.store.api.promotion.PromotionService;
import com.store.api.promotion.model.Promotion;
import com.store.api.promotion.resource.PromotionRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final SaleProductRepository saleProductRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PromotionService promotionService;
    private final PromotionRepository promotionRepository;
    private final ProductService productService;

    public OrderService(SaleProductRepository saleProductRepository,
                        ProductRepository productRepository,
                        OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        PromotionService promotionService,
                        PromotionRepository promotionRepository,
                        ProductService productService
    ) {
        this.saleProductRepository = saleProductRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.promotionService = promotionService;
        this.promotionRepository = promotionRepository;
        this.productService = productService;
    }

    @Transactional(readOnly = true)
    public List<OrderStatusResponse> validateOrder(OrderValidateRequest request) {
        List<OrderDetail> orderDetails = request.getOrderDetails();
        for (OrderDetail orderDetail : orderDetails) {
            if (orderDetail.quantity() <= 0) {
                throw new IllegalArgumentException(ExceptionCode.INVALID_ORDER_QUANTITY.message());
            }
        }
        Long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        boolean isPromotionPeriod = promotionService.validatePromotionPeriod(now);
        List<OrderStatusResponse> responses = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails) {
            OrderStatus orderStatus = validateOrderStatus(orderDetail.productId(), orderDetail.quantity(), isPromotionPeriod);
            OrderStatusResponse response = OrderStatusResponse.toResponse(orderDetail, orderStatus);
            responses.add(response);
        }
        return responses;
    }

    public OrderStatus validateOrderStatus(Long productId, int orderQuantity, boolean isPromotionPeriod) {
        OrderStatus orderStatus;
        SaleProduct saleProduct = saleProductRepository.findByProductId(productId);
        boolean isPromotion = isPromotionPeriod && saleProduct.getPromotionId() != null;
        if (!isEnoughStockToOrder(saleProduct, isPromotion, orderQuantity)) {
            return OrderStatus.OUT_OF_STOCK;
        }
        if (!isPromotion) {
            return OrderStatus.REGULAR;
        }
        orderStatus = validatePromotion(saleProduct, orderQuantity);
        return orderStatus;
    }

    private boolean isEnoughStockToOrder(SaleProduct saleProduct, boolean isPromotion, int orderQuantity) {
        int regularStock = saleProduct.getRegularStock();
        int promotionStock = saleProduct.getPromotionStock();
        if (isPromotion) {
            int totalStock = regularStock + promotionStock;
            return totalStock >= orderQuantity;
        }
        return regularStock >= orderQuantity;
    }

    private OrderStatus validatePromotion(SaleProduct saleProduct, int orderQuantity) {
        Promotion promotion = promotionRepository.findById(saleProduct.getPromotionId());
        // 3. 프로모션 재고 < 주문량 => 일부 정가 결제
        int promotionStock = saleProduct.getPromotionStock();
        if (promotionStock < orderQuantity) {
            return OrderStatus.INSUFFICIENT_PROMOTION_STOCK;
        }
        // 4. 프로모션 재고 > 주문 개수 && 프로모션 buy = 주문개수 => 추가혜택
        if (promotionStock > orderQuantity && promotion.getBuy() == orderQuantity) {
            return OrderStatus.PROMOTION_WITH_COMPLIMENTARY;
        }
        return OrderStatus.PROMOTION; // 5. 나머지 조건은 주문개수만큼 프로모션 재고 차감
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        List<FinalizedOrderDetailRequest> orderDetails = request.getOrderDetailRequests();
        for (FinalizedOrderDetailRequest orderDetail : orderDetails) {
            if (orderDetail.getOrderDetail().quantity() <= 0) {
                throw new IllegalArgumentException(ExceptionCode.INVALID_ORDER_QUANTITY.message());
            }
        }
        List<OrderItem> orderItems = orderDetails.stream()
                .map(this::createOrderItemWithPromotionApplied)
                .toList();
        long totalOrderPrice = orderItems.stream().mapToLong(OrderItem::getTotalPrice).sum();
        int membershipDiscountPrice = 0;
        if (request.isApplyMembershipDiscount()) {
            membershipDiscountPrice = applyMembershipDiscount(orderItems, totalOrderPrice);
        }
        Order order = Order.of(membershipDiscountPrice, totalOrderPrice);
        orderRepository.save(order);
        List<OrderItem> linkedOrderItems = orderItems.stream()
                .map(item -> item.mappingOrder(order.getId()))
                .collect(Collectors.toList());
        orderItemRepository.saveAll(linkedOrderItems);
        orderItems.forEach(productService::decreaseStock);
        return OrderResponse.toResponse(OrderItemResponse.from(linkedOrderItems), order);
    }

    private static int applyMembershipDiscount(List<OrderItem> orderItems, long totalOrderPrice) {
        long promotion = orderItems.stream().mapToLong(OrderItem::getDiscountPrice).sum();
        long remainingPrice = totalOrderPrice - promotion;
        double discountPrice = remainingPrice * Order.MEMBERSHIP_DISCOUNT_RATE;
        return (int) Math.round(Math.min(discountPrice, Order.MEMBERSHIP_DISCOUNT_LIMIT));
    }

    /**
     * 주문 상태 조건에 따라 일반, 프로모션 수량을 적용한다.
     */
    private OrderItem createOrderItemWithPromotionApplied(FinalizedOrderDetailRequest request) {
        OrderDetail orderDetail = request.getOrderDetail();
        OrderItem orderItem = createOrderItem(orderDetail.productId());
        AtomicInteger orderQuantity = new AtomicInteger(orderDetail.quantity()); // 참조 타입으로 초기화
        int promotionQuantity = 0;
        switch (request.getOrderStatus()) {
            case REGULAR -> {}
            case PROMOTION ->
                    promotionQuantity = promotionService.calculatePromotionGetQuantity(orderDetail.productId(), orderDetail.quantity());
            case INSUFFICIENT_PROMOTION_STOCK -> // 일부 정가 구매 여부에 따라
                    promotionQuantity = promotionService.calculateInsufficientPromotionStock(orderDetail, orderQuantity, request.isAcceptInsufficientStockOrder());
            case PROMOTION_WITH_COMPLIMENTARY -> // 프로모션 추가 혜택 여부에 따라
                    promotionQuantity = promotionService.calculatePromotionWithComplimentary(orderDetail, orderQuantity, request.isAcceptComplimentary());
        }
        updateOrderItem(orderItem, orderQuantity.get(), promotionQuantity);
        return orderItem;
    }

    private OrderItem createOrderItem(Long productId) {
        Product product = productRepository.findByProductId(productId);
        return OrderItem.of(product.getId(), product.getName(), product.getPrice(), 0, 0);
    }

    private void updateOrderItem(OrderItem orderItem, int quantity, int promotionQuantity) {
        orderItem.addQuantity(quantity);
        orderItem.updatePromotionDetails(promotionQuantity);
    }

}
