package store.view;

import java.text.NumberFormat;
import store.order.OrderDTO;
import store.order.OrderItemDTO;

public class OutputView {

    public static final String RECEIPT_START = "==============W 편의점================";
    public static final String RECEIPT_COMPLIMENTARY = "=============증     정===============";
    public static final String RECEIPT_LINE = "====================================";
    public static final String PRODUCT_NAME_MESSAGE = "상품명";
    public static final String QUANTITY_MESSAGE = "수량";
    public static final String PRICE_MESSAGE = "금액";
    public static final String TOTAL_PRICE_MESSAGE = "총구매액";
    public static final String DISCOUNT_MESSAGE = "행사할인";
    public static final String MEMBERSHIP_MESSAGE = "멤버십할인";
    public static final String FINAL_PRICE_MESSAGE = "내실돈";
    public static final int PRODUCT_SPACE = 20;
    public static final int PRODUCT_QUANTITY = 8;

    private static StringBuilder sb;

    public void displayOrderReceipt(OrderDTO orderDto) {
        makeReceipt(orderDto);
        System.out.println();
        System.out.println(sb.toString());
    }

    public void makeReceipt(OrderDTO orderDTO) {
        sb = new StringBuilder();
        sb.append(RECEIPT_START).append("\n");

        appendProductDetails(orderDTO);
        appendComplimentary(orderDTO);

        sb.append(RECEIPT_LINE).append("\n");
        appendSummary(orderDTO);
    }

    private void appendProductDetails(OrderDTO orderDTO) {
        makeProduceDetails(PRODUCT_NAME_MESSAGE, QUANTITY_MESSAGE, PRICE_MESSAGE);
        for (OrderItemDTO orderItem : orderDTO.orderItems()) {
            String productName = orderItem.productName();
            int quantity = orderItem.quantity();
            int price = orderItem.price();
            makeProduceDetails(productName, String.valueOf(quantity), getFormattedPrice(quantity * price));
        }
    }

    private void appendComplimentary(OrderDTO orderDTO) {
        sb.append(RECEIPT_COMPLIMENTARY).append("\n");
        for (OrderItemDTO orderItem : orderDTO.orderItems()) {
            if (orderItem.promotionQuantity() < 1) continue;
            String productName = orderItem.productName();
            int promotionQuantity = orderItem.promotionQuantity();
            makeComplimentaryDetails(productName, String.valueOf(promotionQuantity));
        }
    }

    private void appendSummary(OrderDTO orderDTO) {
        int totalOrderPrice = orderDTO.totalOrderPrice();
        int totalQuantity = orderDTO.orderItems().stream().mapToInt(OrderItemDTO::quantity).sum();
        int discountPrice = orderDTO.orderItems().stream().mapToInt(OrderItemDTO::discountPrice).sum();
        int membershipDiscountPrice = orderDTO.membershipDiscountPrice();
        int finalPrice = totalOrderPrice - discountPrice - membershipDiscountPrice;

        makeProduceDetails(TOTAL_PRICE_MESSAGE, getFormattedPrice(totalQuantity), getFormattedPrice(totalOrderPrice));
        sb.append(fillSpacePrice(DISCOUNT_MESSAGE)).append("-").append(getFormattedPrice(discountPrice)).append("\n");
        sb.append(fillSpacePrice(MEMBERSHIP_MESSAGE)).append("-").append(getFormattedPrice(membershipDiscountPrice))
                .append("\n");
        sb.append(fillSpacePrice(FINAL_PRICE_MESSAGE)).append(getFormattedPrice(finalPrice));
    }

    public void makeProduceDetails(String productName, String quantity, String price) {
        sb.append(fillSpaceProductName(productName))
                .append(fillSpaceQuantity(quantity))
                .append(fillSpacePrice(price))
                .append("\n");
    }

    public void makeComplimentaryDetails(String productName, String quantity) {
        sb.append(fillSpaceProductName(productName))
                .append(fillSpaceQuantity(quantity))
                .append("\n");
    }

    public String fillSpaceProductName(String productName) {
        int fillCount = PRODUCT_SPACE - productName.length() + 1;
        return String.format("%-" + fillCount + "s", productName);
    }

    public String fillSpaceQuantity(String quantity) {
        int fillCount = PRODUCT_QUANTITY - quantity.length() + 1;
        return String.format("%-" + fillCount + "s", quantity);
    }

    public String fillSpacePrice(String content) {
        int fillCount = PRODUCT_SPACE + PRODUCT_QUANTITY - content.length() + 1;
        return String.format("%-" + fillCount + "s", content);
    }

    public String getFormattedPrice(int price) {
        return NumberFormat.getInstance().format(price);
    }
}
