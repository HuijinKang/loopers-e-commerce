package com.loopers.domain.coupon;

import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserModel;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class IssuedCouponConcurrencyTest {

    @Autowired private IssuedCouponDomainService issuedCouponDomainService;
    @Autowired private CouponRepository couponRepository;
    @Autowired private IssuedCouponRepository issuedCouponRepository;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() { databaseCleanUp.truncateAllTables(); }

    @DisplayName("동일 발급쿠폰 동시 사용 시 단 1건만 성공해야 한다")
    @Test
    void onlyOneSuccess_whenConcurrentUseSameIssuedCoupon() throws InterruptedException {
        // Given
        UserModel user = userJpaRepository.save(UserModel.of("ccoupon@test.com", "동시성유저", Gender.MALE, "1990-01-01"));
        CouponModel coupon = couponRepository.save(CouponModel.ofFixed("FIX-CC", "fixed", 1000L));
        IssuedCouponModel issued = issuedCouponRepository.save(IssuedCouponModel.of(coupon, user.getId(), IssuedCouponStatus.ISSUED));

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);

        // When
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    issuedCouponDomainService.applyAndUseWithLock(issued.getId(), user.getId(), 10_000L);
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

        // Then
        assertThat(success.get()).isEqualTo(1);
        assertThat(fail.get()).isEqualTo(threadCount - 1);
        assertThat(issuedCouponRepository.findById(issued.getId()).orElseThrow().getStatus())
                .isEqualTo(IssuedCouponStatus.USED);
    }
}
