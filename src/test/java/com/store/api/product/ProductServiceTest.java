package com.store.api.product;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.store.api.order.model.OrderItem;
import com.store.api.product.saleproduct.model.SaleProduct;
import com.store.api.product.saleproduct.resource.SaleProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
    private RedissonClient redissonClient;

    @Mock
    private RLock lock;

    @BeforeEach
    void setup() throws Exception {
        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
    }

    @Test
    void 재고_차감_성공() throws Exception {
        //given
        OrderItem orderItem = OrderItem.of(1L, "콜라", 1000, 3, 0);
        SaleProduct saleProduct = SaleProduct.of(1L, 1L, 10, 10, null);
        given(redissonClient.getLock("lock:product:1")).willReturn(lock);
        given(lock.tryLock(3, 1, TimeUnit.SECONDS)).willReturn(true);
        given(lock.isHeldByCurrentThread()).willReturn(true);
        given(saleProductRepository.findByProductId(1L)).willReturn(saleProduct);
        //when
        productService.decreaseStock(orderItem);
        //then
        verify(saleProductRepository).decreaseRegularStock(3, 1L);
        verify(lock).unlock();
    }

    @Test
    void 재고_부족으로_차감_실패() throws Exception {
        //given
        OrderItem orderItem = OrderItem.of(1L, "콜라", 1000, 3, 0);
        SaleProduct saleProduct = SaleProduct.of(1L, 1L, 0, 0, null);
        given(redissonClient.getLock("lock:product:1")).willReturn(lock);
        given(lock.tryLock(3, 1, TimeUnit.SECONDS)).willReturn(true);
        given(lock.isHeldByCurrentThread()).willReturn(true);
        given(saleProductRepository.findByProductId(1L)).willReturn(saleProduct);
        //when
        //then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.decreaseStock(orderItem));
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
        verify(lock).unlock();
    }

    @Test
    void 락_획득_실패() throws Exception{
        //given
        OrderItem orderItem = OrderItem.of(1L, "콜라", 1000, 3, 0);
        given(redissonClient.getLock("lock:product:1")).willReturn(lock);
        given(lock.tryLock(3, 1, TimeUnit.SECONDS)).willReturn(false);
        //when
        //then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.decreaseStock(orderItem));
        assertTrue(ex.getCause() instanceof IllegalStateException);
    }

}