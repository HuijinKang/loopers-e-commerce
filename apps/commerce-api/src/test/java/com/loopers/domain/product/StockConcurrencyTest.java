package com.loopers.domain.product;

import com.loopers.application.order.OrderFacade;
import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.order.Option;
import com.loopers.domain.point.PointDomainService;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserModel;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.order.OrderV1Dto;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StockConcurrencyTest {

    @Autowired private OrderFacade orderFacade;
    @Autowired private ProductRepository productRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private PointDomainService pointDomainService;
    @Autowired private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("동일 상품 동시 주문 시 초과판매가 발생하지 않는다")
    @Test
    void noOversell_whenConcurrentOrders() throws InterruptedException {
        // Given: 재고 10개인 상품과 15명의 사용자를 준비
        BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
        ProductModel product = productRepository.save(
                ProductModel.of(brand.getId(), "한정", 10_000L, 10, ProductStatus.ON_SALE)
        );

        int users = 15;
        for (int i = 0; i < users; i++) {
            UserModel u = userJpaRepository.save(
                    UserModel.of("stock-" + i + "@test.com", "사용자" + i, Gender.MALE, "1990-01-01")
            );
            pointDomainService.chargePoint(u, 100_000L);
        }

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(users);
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);

        // When: 각 사용자가 동일 상품을 한 개씩 주문
        for (int i = 0; i < users; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    UserModel u = userJpaRepository.findAll().get(idx);

                    Option option = Option.of("BLACK", "FREE");

                    OrderV1Dto.CreateOrderCommand cmd = new OrderV1Dto.CreateOrderCommand(
                            u.getId(),
                            "ORD-C-" + idx,
                            "서울시",
                            null,          // 쿠폰 없음
                            0L,                          // 포인트 미사용
                            List.of(
                                    new OrderV1Dto.CreateOrderCommand.OrderItemCommand(
                                            product.getId(),
                                            option,
                                            1,  // quantity
                                            10_000L     // price
                                    )
                            )
                    );

                    orderFacade.placeOrder(cmd);
                    success.incrementAndGet();
                } catch (Exception e) {
                    fail.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // Then: 10건 성공(재고 소진), 5건 실패, 재고 0
        ProductModel reloaded = productRepository.findById(product.getId()).orElseThrow();
        assertThat(success.get()).isEqualTo(10);
        assertThat(fail.get()).isEqualTo(5);
        assertThat(reloaded.getStock()).isEqualTo(0);
    }
}
