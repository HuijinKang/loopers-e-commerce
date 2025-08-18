package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CouponModelTest {

    @DisplayName("쿠폰 계산은")
    @Nested
    class CalculateDiscount {

        @DisplayName("정액 쿠폰에서 금액이 유효하지 않으면 예외가 발생한다")
        @Test
        void throws_whenFixedAmountInvalid() {
            // arrange
            CouponModel coupon = CouponModel.ofFixed("FIX-INVALID", "fixed invalid", 0L);

            // act & assert
            CoreException ex = assertThrows(CoreException.class, () -> coupon.calculateDiscount(10000L));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("정률 쿠폰에서 비율이 1~100 범위 밖이면 예외가 발생한다")
        @Test
        void throws_whenPercentOutOfRange() {
            // arrange
            CouponModel coupon = CouponModel.ofPercent("PCT-INVALID", "percent invalid", 0);

            // act & assert
            CoreException ex = assertThrows(CoreException.class, () -> coupon.calculateDiscount(10000L));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("정액 쿠폰은 주문 금액에서 고정 금액을 차감한다")
        @Test
        void calculatesFixedCoupon() {
            // arrange
            CouponModel coupon = CouponModel.ofFixed("FIX-1000", "fixed 1000", 1000L);

            // act
            long payable = coupon.calculateDiscount(5000L);

            // assert
            assertThat(payable).isEqualTo(4000L);
        }

        @DisplayName("정률 쿠폰은 주문 금액에서 비율만큼 차감한다")
        @Test
        void calculatesPercentCoupon() {
            // arrange
            CouponModel coupon = CouponModel.ofPercent("PCT-10", "percent 10", 10);

            // act
            long payable = coupon.calculateDiscount(10000L);

            // assert
            assertThat(payable).isEqualTo(9000L);
        }
    }
}
