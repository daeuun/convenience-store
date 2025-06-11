package com.store.api.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.store.api.common.config.RedisConfig;
import com.store.api.order.model.OrderItem;
import com.store.api.product.saleproduct.model.SaleProduct;
import com.store.api.product.saleproduct.resource.SaleProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(RedisConfig.class)
@SpringBootTest
@Slf4j
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private SaleProductRepository saleProductRepository;

    @Test
    void 재고차감_동시성_테스트_5명_중_1명만_성공해야한다() throws InterruptedException {
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<Boolean>> results = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Future<Boolean> result = executorService.submit(() -> {
                try {
                    OrderItem orderItem = OrderItem.of(1L, "콜라", 1000, 10, 0);
                    productService.decreaseStock(orderItem);
                    return true;
                } catch (Exception e) {
                    return false;
                } finally {
                    latch.countDown();
                }
            });
            results.add(result);
        }
        executorService.shutdown();
        latch.await();

        //then
        SaleProduct saleProduct = saleProductRepository.findByProductId(1L);
        assertThat(saleProduct.getRegularStock()).isEqualTo(0);

        long successCount = results.stream().filter(future -> {
            try {
                return future.get();
            } catch (Exception e) {
                return false;
            }
        }).count();
        assertThat(successCount).isEqualTo(1);
    }

}