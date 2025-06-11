package com.store.api.order;

import com.store.api.order.model.OrderRequest;
import com.store.api.order.model.OrderResponse;
import com.store.api.order.model.OrderStatusResponse;
import com.store.api.order.model.OrderValidateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order", description = "주문 API v1")
@RequestMapping("/api/v1/orders")
@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "주문 유효성 검사", description = "구매 가능한 상품의 주문 요청인지 유효성 검사")
    @PostMapping("/validate")
    public ResponseEntity<?> validateOrder(@RequestBody OrderValidateRequest orderRequest) {
        List<OrderStatusResponse> response = orderService.validateOrder(orderRequest);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "주문 생성", description = "상품명, 주문 수량 리스트로 주문을 생성")
    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }
}
