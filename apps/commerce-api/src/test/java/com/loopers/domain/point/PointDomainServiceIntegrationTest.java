package com.loopers.domain.point;

import com.loopers.application.point.PointFacade;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserRepository;
import com.loopers.interfaces.api.point.PointV1Dto;
import com.loopers.support.error.CoreException;
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
class PointDomainServiceIntegrationTest {

    @Autowired
    private PointFacade pointFacade;

    @Autowired
    private PointDomainService pointDomainService;

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

        @DisplayName("해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.")
        @Test
        void returnsPoint_whenUserExists() {
            // arrange
            UserModel user = new UserModel("chulsoo123", "김철수", Gender.MALE, "2000-01-01", "chulsoo@example.com");
            userRepository.save(user);
            UserModel savedUser = userRepository.findByUserId("chulsoo123").orElseThrow();
            PointModel point = PointModel.of(savedUser.getId(), 500L);
            pointRepository.save(point);

            // act
            PointModel result = pointDomainService.getPoint(savedUser.getId());

            // assert;
            assertThat(result.getAmount()).isEqualTo(500L);
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        @Test
        void returnsError_whenUserNotExist() {
            // arrange
            Long invalidId = 999L; // 존재하지 않는 ID

            // act
            PointModel result = pointDomainService.getPoint(invalidId);

            // assert
            assertThat(result).isNull();
        }
    }

    @DisplayName("포인트를 충전할 때,")
    @Nested
    class ChargePoint {

        @DisplayName("존재하지 않는 유저 ID로 충전을 시도하면 실패한다.")
        @Test
        void fails_whenUserNotExist() {
            // arrange
            String invalidId = "none";

            // act & assert
            assertThrows(CoreException.class, () -> pointFacade.chargePoint(invalidId, new PointV1Dto.ChargeRequest(1000L)));
        }
    }
}
