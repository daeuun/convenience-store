package store.order;

import java.util.List;

public record OrderDTO(
        List<OrderItemDTO> orderItems,
        int totalOrderPrice,
        int membershipDiscountPrice
) {
}