package com.loopers.domain.coupon;

import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class IssuedCouponDomainServiceIntegrationTest {

    @Autowired
    private IssuedCouponDomainService issuedCouponDomainService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("발급 쿠폰 적용은")
    @Nested
    class ApplyAndUse {

        @Test
        @DisplayName("소유자가 아니면 FORBIDDEN 예외를 던진다")
        void throws_whenNotOwner() {
            // arrange
            UserModel owner = userJpaRepository.save(UserModel.of("o@test.com", "소유자", Gender.MALE, "2000-01-01"));
            UserModel other = userJpaRepository.save(UserModel.of("x@test.com", "타인", Gender.MALE, "2000-01-01"));

            CouponModel coupon = couponRepository.save(CouponModel.ofFixed("FIX-1000", "fixed 1000", 1000L));

            IssuedCouponModel issued = issuedCouponRepository.save(IssuedCouponModel.of(coupon, owner.getId(), IssuedCouponStatus.ISSUED));

            // act & assert
            CoreException ex = assertThrows(CoreException.class, () ->
                    issuedCouponDomainService.applyAndUseWithLock(issued.getId(), other.getId(), 5000L));
            assertAll(
                () -> assertThat(ex.getErrorType()).isEqualTo(ErrorType.FORBIDDEN),
                () -> assertThat(ex.getMessage()).contains("해당 쿠폰은 사용자에게 발급되지 않았습니다")
            );
        }

        @Test
        @DisplayName("이미 사용된 쿠폰이면 BAD_REQUEST 예외를 던진다 (락 경로)")
        void throws_whenAlreadyUsed() {
            // arrange
            UserModel owner = userRepository.save(UserModel.of("o2@test.com", "소유자", Gender.MALE, "2000-01-01"));

            CouponModel coupon = couponRepository.save(CouponModel.ofFixed("FIX-500", "fixed 500", 500L));

            IssuedCouponModel issued = issuedCouponRepository.save(IssuedCouponModel.of(coupon, owner.getId(), IssuedCouponStatus.USED));

            // act & assert
            CoreException ex = assertThrows(CoreException.class, () ->
                    issuedCouponDomainService.applyAndUseWithLock(issued.getId(), owner.getId(), 5000L));
            assertAll(
                () -> assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST),
                () -> assertThat(ex.getMessage()).contains("사용할 수 없는 쿠폰 상태입니다.")
            );
        }
    }

    @DisplayName("할인 금액 미리보기 및 비활성 쿠폰 검증")
    @Nested
    class PreviewAndValidation {

        @Test
        @DisplayName("previewDiscountAmount는 사용 가능한 쿠폰에서 할인금액을 계산한다")
        void previewDiscountAmount_success() {
            // arrange
            UserModel owner = userJpaRepository.save(UserModel.of("p@test.com", "소유자", Gender.MALE, "2000-01-01"));
            CouponModel coupon = couponRepository.save(CouponModel.ofPercent("PCT-10", "percent 10", 10));
            IssuedCouponModel issued = issuedCouponRepository.save(IssuedCouponModel.of(coupon, owner.getId(), IssuedCouponStatus.ISSUED));

            // act
            long discounted = issuedCouponDomainService.previewDiscountAmount(issued.getId(), owner.getId(), 10000L);

            // assert (10% 할인 → 9000)
            assertThat(discounted).isEqualTo(9000L);
        }

        @Test
        @DisplayName("비활성 쿠폰이면 BAD_REQUEST 예외를 던진다")
        void throws_whenInactive() {
            // arrange
            UserModel owner = userJpaRepository.save(UserModel.of("inactive@test.com", "소유자", Gender.MALE, "2000-01-01"));
            CouponModel coupon = couponRepository.save(CouponModel.ofPercent("PCT-20", "percent 20", 20));
            IssuedCouponModel issued = issuedCouponRepository.save(IssuedCouponModel.of(coupon, owner.getId(), IssuedCouponStatus.USED));

            // act & assert
            CoreException ex = assertThrows(CoreException.class, () ->
                    issuedCouponDomainService.previewDiscountAmount(issued.getId(), owner.getId(), 10000L));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
