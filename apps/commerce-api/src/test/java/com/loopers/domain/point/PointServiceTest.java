package com.loopers.domain.point;

import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private UserRepository userRepository;

    @DisplayName("포인트 충전 시,")
    @Nested
    class ChargePoint {

        @DisplayName("0 이하의 정수로 포인트를 충전 시 실패한다.")
        @Test
        void failsWhenChargingWithZeroOrLessAmount() {
            // given
            String userId = "testUser";
            Long amount = 0L;

            // when
            CoreException exception = assertThrows(CoreException.class, () ->
                    pointService.chargePoint(userId, amount)
            );

            // then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).isEqualTo("충전 금액은 0보다 커야 합니다.");
        }
    }
}
