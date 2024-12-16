package store;

import java.util.List;
import java.util.Objects;
import store.common.ExceptionCode;
import store.order.Order;
import store.order.OrderDTO;
import store.order.OrderItem;
import store.order.OrderItemDTO;
import store.product.SaleProduct;
import store.product.SaleProducts;

public class StoreService {
    private final SaleProducts saleProducts;
    private SaleProduct cachedSaleProduct;

    public StoreService(SaleProducts saleProducts) {
        this.saleProducts = saleProducts;
    }

    private SaleProduct getCachedSaleProduct(String productName) {
        if (cachedSaleProduct == null || !cachedSaleProduct.findByProductName(productName)) {
            cachedSaleProduct = saleProducts.getSaleProductByProductName(productName);
        }
        return cachedSaleProduct;
    }

    public void clearCachedSaleProduct() {
        cachedSaleProduct = null;
    }

    public OrderItem createOrderItem(String productName) {
        return getCachedSaleProduct(productName).createOrderItem(0, 0);
    }

    public String checkOrderStatus(String productName, int orderCount) {
        SaleProduct saleProduct = getCachedSaleProduct(productName);
        boolean isPromotion = saleProduct.isPromotionPeriod();
        if (!saleProduct.isEnoughStockToPurchase(orderCount, isPromotion)) {
            throw new IllegalArgumentException(ExceptionCode.OUT_OF_STOCK.message());
        }
        if (isPromotion) {
            return processToOrderPromotion(saleProduct, orderCount);
        } else {
            return "REGULAR_ORDER";
        }
    }

    private String processToOrderPromotion(SaleProduct saleProduct, int orderCount) {
        // 3. 프로모션 재고 < 주문량 => 일부 정가 결제
        int promotionStock = saleProduct.getPromotionStock();
        if (promotionStock < orderCount) {
            return ExceptionCode.INSUFFICIENT_PROMOTION_STOCK.code();
        }
        // 4. 프로모션 재고 > 주문 개수 && 프로모션 buy = 주문개수 => 추가혜택
        if (promotionStock > orderCount && saleProduct.getPromotionBuyQuantity() == orderCount) {
            return ExceptionCode.CAN_GET_COMPLIMENTARY_QUANTITY.code();
        }
        return "PROMOTION_ORDER"; // 5. 나머지 조건은 주문개수만큼 프로모션 재고 차감
    }

    public void updateOrderItem(OrderItem orderItem, int quantity, int promotionQuantity) {
        orderItem.addQuantity(quantity);
        orderItem.updatePromotionDetails(promotionQuantity);
    }

    public int getInsufficientPromotionStock(String productName, int quantity) {
        return getCachedSaleProduct(productName).insufficientPromotionStock(quantity);
    }

    public int getPromotionGetQuantity(String productName) {
        return getCachedSaleProduct(productName).getPromotionGetQuantity();
    }

    public int calculatePromotionGetQuantity(String productName, int quantity) {
        SaleProduct saleProduct = getCachedSaleProduct(productName);
        int availableQuantity = Math.min(quantity, saleProduct.getPromotionStock());
        return saleProduct.calculateTotalGet(availableQuantity);
    }

    public void orderRegularItem(String productName, int quantity) {
        getCachedSaleProduct(productName).purchaseRegular(quantity);
    }

    public void orderPromotionItem(String productName, int quantity) {
        getCachedSaleProduct(productName).purchasePromotion(quantity);
    }

    public void orderInsufficientItem(String productName, int quantity) {
        int promotionStock = getCachedSaleProduct(productName).getPromotionStock();
        orderPromotionItem(productName, promotionStock);
        orderRegularItem(productName, quantity - promotionStock);
    }

    public Order createOrder(List<OrderItem> orderItems) {
        List<OrderItem> filtered = orderItems.stream()
                .filter(Objects::nonNull)
                .toList();
        return Order.of(filtered);
    }

    public void applyMembershipDiscount(Order order, boolean hasMembership) {
        if (!hasMembership) return;
        order.updateMembershipDiscount();
    }

    public OrderDTO createReceipt(Order order, List<OrderItem> orderItems) {
        List<OrderItemDTO> orderItemDTOS = orderItems.stream()
                .filter(Objects::nonNull)
                .map(OrderItem::toDto)
                .toList();
        return order.toDTO(orderItemDTOS);
    }
}
