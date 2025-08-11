package com.loopers.domain.order;

import com.loopers.application.order.OrderFacade;
import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.coupon.*;
import com.loopers.domain.point.PointDomainService;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductStatus;
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
class OrderCouponConcurrencyTest {

    @Autowired private OrderFacade orderFacade;
    @Autowired private BrandRepository brandRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CouponRepository couponRepository;
    @Autowired private IssuedCouponRepository issuedCouponRepository;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private PointDomainService pointDomainService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() { databaseCleanUp.truncateAllTables(); }

    @DisplayName("동일 발급쿠폰으로 동시에 주문 시 단 1건만 성공한다")
    @Test
    void onlyOneSuccess_whenConcurrentOrdersWithSameIssuedCoupon() throws InterruptedException {
        // arrange
        UserModel user = userJpaRepository.save(UserModel.of("coupon-con@test.com", "쿠폰유저", Gender.MALE, "1990-01-01"));
        BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
        ProductModel product = productRepository.save(ProductModel.of(brand.getId(), "상품", 10000L, 20, ProductStatus.ON_SALE));

        pointDomainService.chargePoint(user, 200_000L);

        CouponModel coupon = couponRepository.save(CouponModel.ofFixed("FIX-CNC", "fixed", 1000L));
        IssuedCouponModel issued = issuedCouponRepository.save(IssuedCouponModel.of(coupon, user.getId(), IssuedCouponStatus.ISSUED));

        int threads = 10;
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
                            "ORD-CNC-" + idx,
                            "서울시",
                            issued.getId(),
                            0L,
                            List.of(new OrderV1Dto.CreateOrderCommand.OrderItemCommand(product.getId(), Option.of("BLK","L"), 1, 10000L))
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
        ProductModel reloaded = productRepository.findById(product.getId()).orElseThrow();
        IssuedCouponModel reIssued = issuedCouponRepository.findById(issued.getId()).orElseThrow();

        assertAll(
                () -> assertThat(success.get()).isEqualTo(1),
                () -> assertThat(failure.get()).isEqualTo(threads - 1),
                () -> assertThat(orderRepository.countByUserId(user.getId())).isEqualTo(1),
                () -> assertThat(reIssued.getStatus()).isEqualTo(IssuedCouponStatus.USED),
                () -> assertThat(reloaded.getStock()).isEqualTo(19)
        );
    }
}
