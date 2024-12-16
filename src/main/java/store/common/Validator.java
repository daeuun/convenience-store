package store.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import store.order.OrderDetail;

public class Validator {
    private static final String REGEX_BRACKET_PATTERN = "\\[(.*?)]";
    private static final Pattern bracketPattern = Pattern.compile(REGEX_BRACKET_PATTERN);

    private static final String REGEX_PRODUCT_ORDER_PATTERN = "([가-힣\\w]+)-(\\d+)";
    private static final Pattern orderPattern = Pattern.compile(REGEX_PRODUCT_ORDER_PATTERN);

    private static final String REGEX_YN_PATTERN = "^[YN]$";
    private static final Pattern anwserPattern = Pattern.compile(REGEX_YN_PATTERN);

    public List<String> extractBracketContent(String input) {
        Matcher matcher = bracketPattern.matcher(input);
        validateDelimiter(input);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }

    private void validateDelimiter(String input) {
        if (!input.matches("(\\[.*?])(,\\[.*?])*")) {
            throw new IllegalArgumentException(ExceptionCode.INVALID_DELIMITER.message());
        }
    }

    public void validateAnswer(String input) {
        if (!anwserPattern.matcher(input).matches()) {
            throw new IllegalArgumentException(ExceptionCode.INVALID_ANSWER_YN.message());
        }
    }

    public List<OrderDetail> parseOrderDetails(List<String> items) {
        List<OrderDetail> orderDetails = new ArrayList<>(List.of());
        for (String item : items) {
            orderDetails.add(createOrderDetail(item));
        }
        return orderDetails;
    }

    private OrderDetail createOrderDetail(String item) {
        Matcher matcher = orderPattern.matcher(item);
        if (matcher.matches()) {
            String productName = matcher.group(1);
            int quantity = Integer.parseInt(matcher.group(2));
            return new OrderDetail(productName, quantity);
        }
        throw new IllegalArgumentException(ExceptionCode.INVALID_ORDER_FORMAT.message());
    }

}
