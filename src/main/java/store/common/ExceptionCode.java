package store.common;

public enum ExceptionCode {
    // product
    INVALID_NUMBER_PRICE("P001", "상품 가격은 양의 정수를 입력해주세요."),
    // sale product
    OUT_OF_STOCK("SP001", "재고가 부족합니다."),
    INSUFFICIENT_PROMOTION_STOCK("SP002", "프로모션 상품 재고가 부족합니다."),
    INSUFFICIENT_REGULAR_STOCK("SP003", "일반 상품 재고가 부족합니다."),
    CAN_GET_COMPLIMENTARY_QUANTITY("SP004", "프로모션 추가 혜택 구매 가능합니다."),
    // input
    INVALID_DELIMITER("I001", "잘못된 구분자를 입력했습니다. 구분자는 쉼표(,)를 입력해주세요."),
    INVALID_ORDER_FORMAT("I002", "잘못된 상품 주문을 입력했습니다."),
    INVALID_ANSWER_YN("I003", "Y 혹은 N을 입력해주세요."),
    OVER_STOCK("I004", "재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");

    private static final String ERROR_PREFIX = "[ERROR] ";
    private final String code;
    private final String message;

    ExceptionCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String message() {
        return ERROR_PREFIX + message;
    }

    public String code() {
        return code;
    }
}
