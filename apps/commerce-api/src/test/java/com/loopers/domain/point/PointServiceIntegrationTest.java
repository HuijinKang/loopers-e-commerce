package com.loopers.domain.point;

import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserRepository;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("포인트를 조회할 때,")
    @Nested
    class GetPoint {

        @DisplayName("해당 ID의 회원이 존재하면, 보유 포인트가 반환된다.")
        @Test
        void returnsPoint_whenUserExists() {
            // arrange
            UserModel user = new UserModel("chulsoo123", "김철수", "M", "2000-01-01", "chulsoo@example.com");
            userRepository.save(user);
            // 영속 상태로 다시 조회
            UserModel savedUser = userRepository.findByUserId("chulsoo123").orElseThrow();
            PointModel point = PointModel.of(savedUser, 500L);
            pointRepository.save(point);

            // act
            PointModel result = pointService.getPoint("chulsoo123");

            // assert
            assertThat(result.getAmount()).isEqualTo(500L);
            assertThat(result.getUser().getUserId()).isEqualTo("chulsoo123");
        }

        @DisplayName("해당 ID의 회원이 존재하지 않으면, 예외가 발생한다.")
        @Test
        void returnsError_whenUserNotExist() {
            // arrange
            String invalidId = "none";

            // act & assert
            CoreException exception = assertThrows(CoreException.class, () -> pointService.getPoint(invalidId));
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("포인트를 충전할 때,")
    @Nested
    class ChargePoint {

        @DisplayName("0 이하의 금액으로 충전하면 실패한다.")
        @Test
        void fails_whenAmountIsZeroOrNegative() {
            // arrange
            UserModel user = new UserModel("chulsoo123", "김철수", "M", "2000-01-01", "chulsoo@example.com");
            userRepository.save(user);

            // act & assert
            assertThrows(CoreException.class, () -> pointService.chargePoint("chulsoo123", 0L));
            assertThrows(CoreException.class, () -> pointService.chargePoint("chulsoo123", -100L));
        }

        @DisplayName("존재하지 않는 유저 ID로 충전을 시도하면 실패한다.")
        @Test
        void fails_whenUserNotExist() {
            // arrange
            String invalidId = "none";

            // act & assert
            assertThrows(CoreException.class, () -> pointService.chargePoint(invalidId, 1000L));
        }

        @DisplayName("존재하는 유저가 1000원을 충전하면, 보유 총량이 증가한다.")
        @Test
        void increasesPoint_whenCharged() {
            // arrange
            UserModel user = new UserModel("chulsoo123", "김철수", "M", "2000-01-01", "chulsoo@example.com");
            userRepository.save(user);

            // act
            pointService.chargePoint("chulsoo123", 1000L);

            // assert
            UserModel savedUser = userRepository.findByUserId("chulsoo123").orElseThrow();
            PointModel point = pointRepository.findByUser(savedUser.getId()).orElseThrow();
            assertThat(point.getAmount()).isEqualTo(1000L);
        }
    }
}
