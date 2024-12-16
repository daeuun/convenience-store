package store;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import store.order.Order;
import store.order.OrderDTO;
import store.order.OrderDetail;
import store.order.OrderItem;
import store.product.SaleProduct;
import store.product.SaleProductDTO;
import store.product.SaleProducts;
import store.view.InputView;
import store.view.OutputView;

public class StoreController {
    private final InputView inputView;
    private final StoreService storeService;
    private final SaleProducts saleProducts;
    private final OutputView outputView;

    public StoreController(InputView inputView, StoreService storeService, OutputView outputView,
                           SaleProducts saleProducts) {
        this.inputView = inputView;
        this.storeService = storeService;
        this.saleProducts = saleProducts;
        this.outputView = outputView;
    }

    public void takeOrder() {
        boolean hasMorePurchase;
        do {
            purchaseItem();
            inputView.displayAdditionalPurchase();
            hasMorePurchase = inputView.getAnswer().equalsIgnoreCase("Y");
            System.out.println();
        } while (hasMorePurchase);
    }

    public void purchaseItem() {
        inputView.welcomeStore();
        inputView.displaySaleProduct(parseSaleProductDto(saleProducts));
        List<OrderDetail> orderDetails = inputView.getOrderDetails();
        List<OrderItem> orderItems = createOrderItems(orderDetails);
        Order order = storeService.createOrder(orderItems);
        applyMembershipDiscount(order);
        OrderDTO receipt = storeService.createReceipt(order, orderItems);
        outputView.displayOrderReceipt(receipt);
    }

    private List<OrderItem> createOrderItems(List<OrderDetail> orderDetails) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails) {
            try {
                OrderItem orderItem = processOrderDetail(orderDetail);
                orderItems.add(orderItem);
            } finally {
                storeService.clearCachedSaleProduct();
            }
        }
        return orderItems;
    }

    private OrderItem processOrderDetail(OrderDetail orderDetail) {
        OrderItem orderItem = storeService.createOrderItem(orderDetail.productName());
        AtomicInteger orderQuantity = new AtomicInteger(orderDetail.quantity()); // 참조 타입으로 초기화
        int promotionQuantity = 0;
        String status = storeService.checkOrderStatus(orderDetail.productName(), orderDetail.quantity());
        switch (status) {
            case "REGULAR_ORDER" ->
                    processRegularOrder(orderDetail.productName(), orderDetail.quantity());
            case "PROMOTION_ORDER" ->
                    promotionQuantity = processPromotionOrder(orderDetail.productName(), orderDetail.quantity());
            case "SP002" -> // 일부 정가 구매
                    promotionQuantity = processInsufficientPromotionStock(orderDetail, orderQuantity);
            case "SP004" -> // 프로모션 추가 혜택
                    promotionQuantity = processPromotionWithComplimentary(orderDetail, orderQuantity);
        }
        storeService.updateOrderItem(orderItem, orderQuantity.get(), promotionQuantity);
        return orderItem;
    }

    private void processRegularOrder(String productName, int quantity) {
        storeService.orderRegularItem(productName, quantity);
    }

    private int processPromotionOrder(String productName, int quantity) {
        int promotionQuantity = storeService.calculatePromotionGetQuantity(productName, quantity);
        storeService.orderPromotionItem(productName, quantity);
        return promotionQuantity;
    }

    private int processInsufficientPromotionStock(OrderDetail orderDetail, AtomicInteger orderQuantity) {
        int insufficientStock = storeService.getInsufficientPromotionStock(orderDetail.productName(), orderDetail.quantity());
        inputView.displayInsufficientQuantity(orderDetail.productName(), insufficientStock);
        String answer = inputView.getAnswer();
        if (answer.equals("Y")) {
            int promotionGetQuantity = storeService.calculatePromotionGetQuantity(orderDetail.productName(), orderDetail.quantity());
            storeService.orderInsufficientItem(orderDetail.productName(), orderDetail.quantity());
            return promotionGetQuantity;
        } else {
            orderQuantity.updateAndGet(current -> current - insufficientStock);
            storeService.orderPromotionItem(orderDetail.productName(), insufficientStock);
            return 0;
        }
    }

    private int processPromotionWithComplimentary(OrderDetail orderDetail, AtomicInteger orderQuantity) {
        int complimentaryQuantity = storeService.getPromotionGetQuantity(orderDetail.productName());
        inputView.displayComplimentary(orderDetail.productName(), complimentaryQuantity);
        String answer = inputView.getAnswer();
        if (answer.equals("Y")) {
            int updatedQuantity = orderDetail.quantity() + complimentaryQuantity;
            orderQuantity.addAndGet(complimentaryQuantity);
            int promotionQuantity = storeService.calculatePromotionGetQuantity(orderDetail.productName(), updatedQuantity);
            storeService.orderPromotionItem(orderDetail.productName(), updatedQuantity);
            return promotionQuantity;
        } else {
            storeService.orderPromotionItem(orderDetail.productName(), orderDetail.quantity());
            return 0;
        }
    }

    private void applyMembershipDiscount(Order order) {
        inputView.displayMembershipDiscount();
        boolean hasMembership = inputView.getAnswer().equalsIgnoreCase("Y");
        storeService.applyMembershipDiscount(order, hasMembership);
    }

    private List<SaleProductDTO> parseSaleProductDto(SaleProducts saleProducts) {
        return saleProducts.getSaleProducts().stream()
                .map(SaleProduct::toDTO)
                .collect(Collectors.toList());
    }
}
