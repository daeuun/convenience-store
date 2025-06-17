package com.store.api.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.store.api.common.exception.ResourceNotFoundException;
import com.store.api.order.model.OrderDetail;
import com.store.api.order.model.OrderStatus;
import com.store.api.order.model.OrderStatusResponse;
import com.store.api.order.model.OrderValidateRequest;
import com.store.api.order.resource.OrderItemRepository;
import com.store.api.order.resource.OrderRepository;
import com.store.api.product.ProductService;
import com.store.api.product.model.Product;
import com.store.api.product.resource.ProductRepository;
import com.store.api.product.saleproduct.model.SaleProduct;
import com.store.api.product.saleproduct.resource.SaleProductRepository;
import com.store.api.promotion.PromotionService;
import com.store.api.promotion.model.Promotion;
import com.store.api.promotion.resource.PromotionRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock private SaleProductRepository saleProductRepository;
    @Mock private ProductRepository productRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private PromotionService promotionService;
    @Mock private PromotionRepository promotionRepository;
    @Mock private ProductService productService;

    @Test
    public void 주문_수량이_재고보다_많은_경우_재고_부족() {
        //given
        OrderDetail orderDetail = new OrderDetail(1L, 1);
        OrderValidateRequest request = new OrderValidateRequest(List.of(orderDetail));
        Product product = new Product(1L, "콜라", 1000);
        long endAt = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + 1;
        Promotion promotion = new Promotion(1L, "promotion", 2, 1, 0L, endAt);
        SaleProduct saleProduct = SaleProduct.of(1L, product.getId(), 0, 0, promotion.getId());

        given(promotionService.validatePromotionPeriod(anyLong())).willReturn(true);
        given(saleProductRepository.findByProductId(1L)).willReturn(saleProduct);

        //when
        List<OrderStatusResponse> result = orderService.validateOrder(request);

        //when then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getOrderStatus()).isEqualTo(OrderStatus.OUT_OF_STOCK);
    }

    @Test
    public void 프로모션_주문_수량이_재고보다_많은_경우_일부_정가_결제() {
        //given
        OrderDetail orderDetail = new OrderDetail(1L, 3);
        OrderValidateRequest request = new OrderValidateRequest(List.of(orderDetail));
        Product product = new Product(1L, "콜라", 1000);
        long endAt = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + 1;
        Promotion promotion = new Promotion(1L, "promotion", 2, 1, 0L, endAt);
        SaleProduct saleProduct = SaleProduct.of(1L, product.getId(), 1, 2, promotion.getId());

        given(promotionService.validatePromotionPeriod(anyLong())).willReturn(true);
        given(saleProductRepository.findByProductId(1L)).willReturn(saleProduct);

        //when
        List<OrderStatusResponse> result = orderService.validateOrder(request);

        //when then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getOrderStatus()).isEqualTo(OrderStatus.INSUFFICIENT_PROMOTION_STOCK);
    }

    @Test
    public void 프로모션_기간내_주문_상태는_프로모션() {
        //given
        OrderDetail orderDetail = new OrderDetail(1L, 1);
        OrderValidateRequest request = new OrderValidateRequest(List.of(orderDetail));
        Product product = new Product(1L, "콜라", 1000);
        long endAt = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + 1;
        Promotion promotion = new Promotion(1L, "promotion", 2, 1, 0L, endAt);
        SaleProduct saleProduct = SaleProduct.of(1L, product.getId(), 10, 10, promotion.getId());

        given(promotionService.validatePromotionPeriod(anyLong())).willReturn(true);
        given(saleProductRepository.findByProductId(1L)).willReturn(saleProduct);
        given(promotionRepository.findById(1L)).willReturn(promotion);

        //when
        List<OrderStatusResponse> result = orderService.validateOrder(request);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getOrderStatus()).isEqualTo(OrderStatus.PROMOTION);
    }

    @Test
    public void 프로모션_기간이_아닌_주문_상태는_일반() {
        //given
        OrderDetail orderDetail = new OrderDetail(1L, 1);
        OrderValidateRequest request = new OrderValidateRequest(List.of(orderDetail));
        Product product = new Product(1L, "콜라", 1000);
        long endAt = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + 1;
        Promotion promotion = new Promotion(1L, "promotion", 2, 1, 0L, endAt);
        SaleProduct saleProduct = SaleProduct.of(1L, product.getId(), 10, 10, promotion.getId());

        given(promotionService.validatePromotionPeriod(anyLong())).willReturn(false);
        given(saleProductRepository.findByProductId(1L)).willReturn(saleProduct);

        //when
        List<OrderStatusResponse> result = orderService.validateOrder(request);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getOrderStatus()).isEqualTo(OrderStatus.REGULAR);
    }

    @Test
    public void 프로모션_조건_주문_아이템_수량_미달시_추가_혜택_적용() {
        //given
        OrderDetail orderDetail = new OrderDetail(1L, 2);
        OrderValidateRequest request = new OrderValidateRequest(List.of(orderDetail));
        Product product = new Product(1L, "콜라", 1000);
        long endAt = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + 1;
        Promotion promotion = new Promotion(1L, "promotion", 2, 1, 0L, endAt);
        SaleProduct saleProduct = SaleProduct.of(1L, product.getId(), 10, 10, promotion.getId());

        given(promotionService.validatePromotionPeriod(anyLong())).willReturn(true);
        given(saleProductRepository.findByProductId(1L)).willReturn(saleProduct);
        given(promotionRepository.findById(1L)).willReturn(promotion);

        //when
        List<OrderStatusResponse> result = orderService.validateOrder(request);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getOrderStatus()).isEqualTo(OrderStatus.PROMOTION_WITH_COMPLIMENTARY);
    }

    @Test
    public void 여러_주문에서_하나는_재고_부족_하나는_프로모션_조건_충족() {
        //given
        OrderDetail orderDetailOfInvalidStock = new OrderDetail(1L, 10); // 재고부족
        OrderDetail orderDetailOfPromotion = new OrderDetail(2L, 2);
        OrderValidateRequest request = new OrderValidateRequest(List.of(orderDetailOfInvalidStock, orderDetailOfPromotion));

        Product product1 = new Product(1L, "콜라", 1000);
        Product product2 = new Product(2L, "사이다", 1000);
        long endAt = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + 1;
        Promotion promotion = new Promotion(1L, "promotion", 1, 1, 0L, endAt);
        SaleProduct saleProduct1 = SaleProduct.of(1L, product1.getId(), 1, 10, null);
        SaleProduct saleProduct2 = SaleProduct.of(2L, product1.getId(), 10, 10, promotion.getId());

        given(promotionService.validatePromotionPeriod(anyLong())).willReturn(true);
        given(saleProductRepository.findByProductId(1L)).willReturn(saleProduct1);
        given(saleProductRepository.findByProductId(2L)).willReturn(saleProduct2);
        given(promotionRepository.findById(1L)).willReturn(promotion);

        //when
        List<OrderStatusResponse> result = orderService.validateOrder(request);

        //then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getOrderStatus()).isEqualTo(OrderStatus.OUT_OF_STOCK);
        assertThat(result.get(1).getOrderStatus()).isEqualTo(OrderStatus.PROMOTION);
    }

    @Test
    public void 주문_수량이_0이거나_음수라면_예외처리() {
        //given
        OrderDetail orderDetail1 = new OrderDetail(1L, 0);
        OrderDetail orderDetail2 = new OrderDetail(2L, -1);
        OrderValidateRequest request = new OrderValidateRequest(List.of(orderDetail1, orderDetail2));

        Product product1 = new Product(1L, "콜라", 1000);
        Product product2 = new Product(2L, "사이다", 1000);
        long endAt = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + 1;
        Promotion promotion = new Promotion(1L, "promotion", 2, 1, 0L, endAt);
        SaleProduct saleProduct1 = SaleProduct.of(1L, product1.getId(), 10, 10, promotion.getId());
        SaleProduct saleProduct2 = SaleProduct.of(2L, product1.getId(), 10, 10, promotion.getId());

        //when & then
        assertThrows(IllegalArgumentException.class, () -> orderService.validateOrder(request));
    }

    @Test
    public void 존재하지_않는_상품_주문하면_예외_발생() {
        //given
        OrderDetail orderDetail = new OrderDetail(1L, 1);
        OrderValidateRequest request = new OrderValidateRequest(List.of(orderDetail));

        given(promotionService.validatePromotionPeriod(anyLong())).willReturn(false);
        given(saleProductRepository.findByProductId(1L)).willThrow(new ResourceNotFoundException("SaleProduct", 1L));

        //when then
        assertThrows(ResourceNotFoundException.class, () -> orderService.validateOrder(request));
    }
}