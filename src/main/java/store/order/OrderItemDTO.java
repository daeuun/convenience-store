package store.order;

public record OrderItemDTO(String productName, int price, int quantity, int promotionQuantity, int discountPrice) {
}
