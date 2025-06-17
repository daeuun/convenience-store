package com.store.api.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.store.api.common.exception.ResourceNotFoundException;
import com.store.api.order.model.OrderItem;
import com.store.api.product.model.Product;
import com.store.api.product.model.ProductListResponse;
import com.store.api.product.model.ProductResponse;
import com.store.api.product.model.ProductSearchParam;
import com.store.api.product.model.ProductUpdateRequest;
import com.store.api.product.model.StockResponse;
import com.store.api.product.resource.ProductRepository;
import com.store.api.product.saleproduct.model.SaleProduct;
import com.store.api.product.saleproduct.resource.SaleProductRepository;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private SaleProductRepository saleProductRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock lock;

    private void givenLockIsAcquired() throws Exception {
        given(redissonClient.getLock(anyString())).willReturn(lock);
        given(lock.tryLock(3, 1, TimeUnit.SECONDS)).willReturn(true);
        given(lock.isHeldByCurrentThread()).willReturn(true);
    }

    @Test
    void 일반_주문_재고_차감_성공() throws Exception {
        //given
        OrderItem orderItem = OrderItem.of(1L, "콜라", 1000, 3, 0);
        SaleProduct saleProduct = SaleProduct.of(1L, 1L, 10, 10, null);

        givenLockIsAcquired();
        given(saleProductRepository.findByProductId(1L)).willReturn(saleProduct);

        //when
        productService.decreaseStock(orderItem);

        //then
        verify(saleProductRepository).decreaseRegularStock(3, 1L);
        verify(lock).unlock();
    }

    @Test
    void 일반_주문_재고_부족으로_차감_실패() throws Exception {
        //given
        OrderItem orderItem = OrderItem.of(1L, "콜라", 1000, 3, 0);
        SaleProduct saleProduct = SaleProduct.of(1L, 1L, 0, 0, null);

        givenLockIsAcquired();
        given(saleProductRepository.findByProductId(1L)).willReturn(saleProduct);

        //when & then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.decreaseStock(orderItem));
        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
        verify(lock).unlock();
    }

    @Test
    void 락_획득_실패() throws Exception {
        //given
        OrderItem orderItem = OrderItem.of(1L, "콜라", 1000, 3, 0);

        given(redissonClient.getLock("lock:product:1")).willReturn(lock);
        given(lock.tryLock(3, 1, TimeUnit.SECONDS)).willReturn(false);

        //when & then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.decreaseStock(orderItem));
        assertInstanceOf(IllegalStateException.class, ex.getCause());
    }

    @Test
    void 락_예외_발생하면_락이_해제된다() throws Exception {
        //given
        OrderItem orderItem = OrderItem.of(1L, "콜라", 1000, 3, 0);
        SaleProduct saleProduct = SaleProduct.of(1L, 1L, 0, 0, null);

        givenLockIsAcquired();
        given(saleProductRepository.findByProductId(anyLong())).willThrow(new IllegalArgumentException("락 예외"));

        // when & then
        assertThrows(RuntimeException.class, () -> productService.decreaseStock(orderItem));
        verify(lock).unlock();
    }

    @Test
    void 상품_조회_성공() {
        // given
        Product product = new Product(1L, "콜라", 1000);
        given(productRepository.findByProductId(1L)).willReturn(product);

        // when
        ProductResponse result = productService.getProduct(1L);

        // then
        assertThat(result.getId()).isEqualTo(product.getId());
        assertThat(result.getName()).isEqualTo(product.getName());
        assertThat(result.getPrice()).isEqualTo(product.getPrice());
    }

    @Test
    void 상품_재고_조회_성공() {
        // given
        Product product = new Product(1L, "콜라", 1000);
        SaleProduct saleProduct = SaleProduct.of(1L, product.getId(), 10, 10, null);
        given(saleProductRepository.findByProductId(product.getId())).willReturn(saleProduct);

        // when
        StockResponse result = productService.getStocks(1L);

        // then
        assertThat(result.getRegularStock()).isEqualTo(saleProduct.getRegularStock());
        assertThat(result.getPromotionStock()).isEqualTo(saleProduct.getPromotionStock());
    }

    @Test
    void 상품_검색_조건에_맞는_상품_목록_조회_성공() {
        // given
        ProductSearchParam searchParam = new ProductSearchParam(0, 10);
        List<Product> products = List.of(
                new Product(1L, "콜라1", 1000),
                new Product(2L, "콜라2", 2000),
                new Product(3L, "콜라3", 3000),
                new Product(4L, "콜라4", 4000),
                new Product(5L, "콜라5", 5000));
        given(productRepository.findAll(searchParam)).willReturn(products);

        // when
        ProductListResponse result = productService.getProducts(searchParam);

        // then
        assertThat(result.getProducts()).hasSize(products.size());
        assertThat(result.getProducts())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(
                        products.stream()
                                .map(ProductResponse::from)
                                .toList()
                );
        for (int i = 0; i < products.size(); i++) {
            assertThat(result.getProducts().get(i).getId()).isEqualTo(products.get(i).getId());
            assertThat(result.getProducts().get(i).getName()).isEqualTo(products.get(i).getName());
            assertThat(result.getProducts().get(i).getPrice()).isEqualTo(products.get(i).getPrice());
        }
    }

    @Test
    void 상품_검색_조건에_맞는_상품_없으면_빈_리스트_반환() {
        // given
        ProductSearchParam searchParam = new ProductSearchParam(0, 10);
        given(productRepository.findAll(searchParam)).willReturn(Collections.emptyList());

        // when
        ProductListResponse result = productService.getProducts(searchParam);

        // then
        assertThat(result.getProducts()).isEmpty();
    }

    @Test
    void 상품_업데이트_성공() {
        // given
        ProductUpdateRequest request = new ProductUpdateRequest();
        Product product = new Product(1L, "콜라", 1000);
        given(productRepository.findByProductId(product.getId())).willReturn(product);
        doNothing().when(productRepository).update(product.getId(), request);

        // when
        ProductResponse result = productService.updateProduct(product.getId(), request);

        // then
        verify(productRepository).update(product.getId(), request);
        assertThat(result.getId()).isEqualTo(product.getId());
        assertThat(result.getName()).isEqualTo(product.getName());
        assertThat(result.getPrice()).isEqualTo(product.getPrice());
    }

    @Test
    void 상품_삭제_성공() {
        // given
        Product product = new Product(1L, "콜라", 1000);
        given(productRepository.findByProductId(product.getId())).willReturn(product);
        doNothing().when(productRepository).delete(product.getId());

        // when
        productService.deleteProduct(product.getId());

        // then
        verify(productRepository).delete(product.getId());
    }

    @Test
    void 상품_조회시_상품이_존재하지_않으면_예외() {
        //given
        Long productId = 1L;
        given(productRepository.findByProductId(productId)).willThrow(new ResourceNotFoundException("Product", productId));

        //when & then
        assertThrows(ResourceNotFoundException.class, () -> productService.getProduct(productId));
    }

    @Test
    void 재고_조회시_판매_상품이_존재하지_않으면_예외() {
        //given
        Long productId = 1L;
        given(saleProductRepository.findByProductId(productId)).willThrow(new ResourceNotFoundException("SaleProduct", productId));
        //when & then
        assertThrows(ResourceNotFoundException.class, () -> productService.getStocks(productId));
    }

    @Test
    void 상품_목록_조회시_검색조건이_null이면_예외() {
        //given
        given(productRepository.findAll(null)).willThrow(new IllegalArgumentException("Invalid productSearchParam"));
        //when & then
        assertThrows(IllegalArgumentException.class, () -> productService.getProducts(null));
    }

    @Test
    void 상품_업데이트_시_상품이_존재하지_않으면_예외() {
        //given
        given(productRepository.findByProductId(1L)).willThrow(new ResourceNotFoundException("Product", 1L));
        //when & then
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, new ProductUpdateRequest()));
    }

    @Test
    void 상품_삭제_시_상품이_존재하지_않으면_예외() {
        //given
        Long productId = 1L;
        given(productRepository.findByProductId(productId)).willThrow(new ResourceNotFoundException("Product", productId));
        //when & then
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(productId));
    }

}