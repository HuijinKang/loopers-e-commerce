package com.loopers.domain.order;

import com.loopers.application.order.OrderFacade;
import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.point.PointDomainService;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductRepository;
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
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class OrderPointConcurrencyTest {

    @Autowired private OrderFacade orderFacade;
    @Autowired private BrandRepository brandRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private PointDomainService pointDomainService;
    @Autowired private PointRepository pointRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() { databaseCleanUp.truncateAllTables(); }

    @DisplayName("같은 유저가 동시에 포인트를 사용한 주문을 해도 초과 사용이 방지된다")
    @Test
    void preventPointOveruse_whenConcurrentOrdersBySameUser() throws InterruptedException {
        // arrange
        UserModel user = userJpaRepository.save(UserModel.of("point-con@test.com", "포인트유저", Gender.MALE, "1990-01-01"));
        BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
        ProductModel product = productRepository.save(ProductModel.of(brand.getId(), "상품", 10_000L, 20));

        // 총 보유 15_000 충전 → 각 주문은 10_000 사용 요청. 동시 2건이면 1건 실패 기대
        pointDomainService.chargePoint(user, 15_000L);

        int threads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();

        // act
        for (int i = 0; i < threads; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    start.await();
                    OrderV1Dto.CreateOrderCommand cmd = new OrderV1Dto.CreateOrderCommand(
                            user.getId(),
                            "ORD-PNC-" + idx,
                            "서울시",
                            null,
                            10_000L, // 포인트 사용
                            List.of(new OrderV1Dto.CreateOrderCommand.OrderItemCommand(product.getId(), Option.of("BLK","L"), 1, 10_000L))
                    );
                    orderFacade.placeOrder(cmd);
                    success.incrementAndGet();
                } catch (Exception e) {
                    failure.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        done.await();
        executor.shutdown();

        // assert
        long finalPoint = pointRepository.findByUserId(user.getId()).orElseThrow().getAmount();
        assertAll(
                () -> assertThat(success.get()).isEqualTo(1),
                () -> assertThat(failure.get()).isEqualTo(1),
                () -> assertThat(orderRepository.countByUserId(user.getId())).isEqualTo(1),
                () -> assertThat(finalPoint).isGreaterThanOrEqualTo(5_000L)
        );
    }
}
