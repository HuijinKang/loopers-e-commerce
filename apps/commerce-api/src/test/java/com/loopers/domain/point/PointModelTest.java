package com.loopers.domain.point;

import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserModel;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PointModelTest {

    @DisplayName("포인트 충전 시,")
    @Nested
    class ChargePoint {

        @DisplayName("0 이하의 정수로 포인트를 충전 시 실패한다.")
        @Test
        void failsWhenChargingWithZeroOrLessAmount() {
            // arrange
            UserModel user = new UserModel("chulsoo123", "김철수", Gender.MALE, "2000-01-01", "chulsoo@example.com");
            PointModel point = new PointModel(user, 1000L);

            // act & assert
            CoreException exception = assertThrows(CoreException.class,
                    () -> point.charge(0L));

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).isEqualTo("충전 금액은 0보다 커야 합니다.");
        }
    }
}
