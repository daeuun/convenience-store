package store.view;

import java.text.NumberFormat;
import java.util.List;
import java.util.Scanner;
import store.common.Validator;
import store.order.OrderDetail;
import store.product.SaleProductDTO;

public class InputView {
    private final static String WELCOME_MESSAGE = "안녕하세요. W편의점입니다.";
    private final static String INTRODUCE_PRODUCT_MESSAGE = "현재 보유하고 있는 상품입니다.";
    private final static String DISPLAY_SALE_PRODUCT = "- %s %s원 %s %s";
    private final static String INPUT_PRODUCT_DETAIL_MESSAGE = "구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])";
    private final static String MEMBERSHIP_DISCOUNT_MESSAGE = "멤버십 할인을 받으시겠습니까? (Y/N)";
    private final static String ADDITIONAL_PURCHASE_MESSAGE = "감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)";
    private final static String PROMOTION_NOT_APPLY_MESSAGE = "현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)";
    private final static String COMPLIMENTARY_MESSAGE = "현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)";

    private final Validator validator;

    public InputView(Validator validator) {
        this.validator = validator;
    }

    public void welcomeStore() {
        System.out.println(WELCOME_MESSAGE);
        System.out.println(INTRODUCE_PRODUCT_MESSAGE);
        System.out.println();
    }

    public void displaySaleProduct(List<SaleProductDTO> saleProducts) {
        for (SaleProductDTO saleProduct : saleProducts) {
            if (saleProduct.promotion() != null) {
                String formatted = formatPromotionSaleProductToDisplay(saleProduct);
                System.out.println(formatted);
            }
            String formatted = formatSaleProductToDisplay(saleProduct);
            System.out.println(formatted);
        }
    }

    private String formatPromotionSaleProductToDisplay(SaleProductDTO saleProduct) {
        return String.format(DISPLAY_SALE_PRODUCT,
                saleProduct.product().name(),
                getFormattedPrice(saleProduct.product().price()),
                parseStock(saleProduct.promotionStock()),
                saleProduct.promotion().name()
        );
    }

    private String formatSaleProductToDisplay(SaleProductDTO saleProduct) {
        return String.format(DISPLAY_SALE_PRODUCT,
                saleProduct.product().name(),
                getFormattedPrice(saleProduct.product().price()),
                parseStock(saleProduct.regularStock()),
                ""
        );
    }

    public String getFormattedPrice(int price) {
        return NumberFormat.getInstance().format(price);
    }

    private String parseStock(int stockQuantity) {
        if (stockQuantity == 0) {
            return "재고 없음";
        }
        return stockQuantity + "개";
    }

    public List<OrderDetail> getOrderDetails() {
        System.out.println();
        System.out.println(INPUT_PRODUCT_DETAIL_MESSAGE);
        while (true) {
            try {
                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine();
                return validateOrderDetails(input);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private List<OrderDetail> validateOrderDetails(String input) {
        String trimmedInput = input.replaceAll(" ", "");
        List<String> items = validator.extractBracketContent(trimmedInput);
        return validator.parseOrderDetails(items);
    }

    public void displayComplimentary(String productName, int quantity) {
        System.out.println();
        String formatted = String.format(COMPLIMENTARY_MESSAGE, productName, quantity);
        System.out.println(formatted);
    }

    public String getAnswer() {
        while (true) {
            try {
                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine();
                return validateAnswer(input);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private String validateAnswer(String input) {
        String trimmedInput = input.replaceAll(" ", "");
        validator.validateAnswer(trimmedInput);
        return trimmedInput;
    }

    public void displayInsufficientQuantity(String productName, int quantity) {
        System.out.println();
        String formatted = String.format(PROMOTION_NOT_APPLY_MESSAGE, productName, quantity);
        System.out.println(formatted);
    }

    public void displayMembershipDiscount() {
        System.out.println();
        System.out.println(MEMBERSHIP_DISCOUNT_MESSAGE);
    }

    public void displayAdditionalPurchase() {
        System.out.println();
        System.out.println(ADDITIONAL_PURCHASE_MESSAGE);
    }
}
