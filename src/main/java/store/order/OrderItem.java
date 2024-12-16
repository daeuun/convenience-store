package store.order;

public class OrderItem {
    private final String productName;
    private final int price;
    private int quantity; // 전체수량
    private int promotionQuantity; // 증정수량
    private int discountPrice; // 증정에 따른 할인 금액

    private OrderItem(String productName, int price, int quantity, int promotionQuantity) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.promotionQuantity = promotionQuantity;
    }

    public static OrderItem of(String productName, int price, int quantity, int promotionQuantity) {
        return new OrderItem(productName, price, quantity, promotionQuantity);
    }

    public int getTotalPrice() {
        return this.price * this.quantity;
    }

    public int getDiscountPrice() {
        return this.discountPrice;
    }

    public OrderItemDTO toDto() {
        return new OrderItemDTO(this.productName, this.price, this.quantity, this.promotionQuantity, this.discountPrice);
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void updatePromotionDetails(int promotionQuantity) {
        this.promotionQuantity += promotionQuantity;
        int discount = price * promotionQuantity;
        this.discountPrice += discount;
    }
}
