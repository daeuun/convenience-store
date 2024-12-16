package store.order;

import java.util.List;

public class Order {
    public static final double MEMBERSHIP_DISCOUNT_RATE = 0.3;
    public static final double MEMBERSHIP_DISCOUNT_LIMIT = 8_000;

    private Long id;
    private final List<OrderItem> orderItems;
    private int membershipDiscountPrice;
    private final int totalOrderPrice;

    private Order(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        this.totalOrderPrice = calculateTotalOrderPrice();
    }

    public static Order of(List<OrderItem> orderItems) {
        return new Order(orderItems);
    }

    private int calculateTotalOrderPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }

    public void updateMembershipDiscount() {
        int remainingPrice = calculateTotalOrderPrice() - calculatePromotionPrice();
        double discountPrice = remainingPrice * MEMBERSHIP_DISCOUNT_RATE;
        this.membershipDiscountPrice = (int) Math.round(Math.min(discountPrice, MEMBERSHIP_DISCOUNT_LIMIT));
    }

    private int calculatePromotionPrice() {
        return orderItems.stream().mapToInt(OrderItem::getDiscountPrice).sum();
    }

    public OrderDTO toDTO(List<OrderItemDTO> orderItems) {
        return new OrderDTO(orderItems, totalOrderPrice, membershipDiscountPrice);
    }
}
