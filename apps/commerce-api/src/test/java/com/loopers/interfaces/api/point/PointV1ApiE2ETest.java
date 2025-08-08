package com.loopers.interfaces.api.point;

import com.loopers.domain.point.PointModel;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PointV1ApiE2ETest {

    private static final String ENDPOINT_GET_POINT = "/api/v1/points";
    private static final String ENDPOINT_CHARGE_POINT = "/api/v1/points/charge";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    private UserModel testUser;

    @BeforeEach
    void setUp() {
        UserModel newUser = new UserModel("testUser", "Test Name", Gender.MALE, "2000-01-01", "test@example.com");
        userRepository.save(newUser);
        testUser = userRepository.findByUserId(newUser.getUserId()).orElseThrow();
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("GET /api/v1/points")
    @Nested
    class GetPoint {

        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
        @Test
        void returnsPoint_whenSuccessful() {
            // arrange
            PointModel initialPoint = PointModel.of(testUser.getId(), 5000L);
            pointRepository.save(initialPoint);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", testUser.getUserId());
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            // act
            ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<PointV1Dto.PointResponse>> response = testRestTemplate.exchange(
                    ENDPOINT_GET_POINT, HttpMethod.GET, requestEntity, responseType
            );

            // assert
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().amount()).isEqualTo(5000L)
            );
        }

        @DisplayName("X-USER-ID 헤더가 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void returnsBadRequest_whenXUserIdHeaderIsMissing() {
            // arrange
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            // act
            ResponseEntity<String> response = testRestTemplate.exchange(
                    ENDPOINT_GET_POINT, HttpMethod.GET, requestEntity, String.class
            );

            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("POST /api/v1/points/charge")
    @Nested
    class ChargePoint {

        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        void returnsChargedPoint_whenExistingUserCharges() {
            // arrange
            PointV1Dto.ChargeRequest request = new PointV1Dto.ChargeRequest(1000L);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", testUser.getUserId());
            HttpEntity<PointV1Dto.ChargeRequest> requestEntity = new HttpEntity<>(request, headers);

            // act
            ResponseEntity<String> response = testRestTemplate.exchange(
                    ENDPOINT_CHARGE_POINT, HttpMethod.POST, requestEntity, String.class
            );

            // assert
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody()).contains("1000")
            );

            PointModel updatedPoint = pointRepository.findByUserId(testUser.getId()).orElseThrow();
            assertThat(updatedPoint.getAmount()).isEqualTo(1000L);
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void returnsBadRequest_whenNonExistentUserRequestsCharge() {
            // arrange
            PointV1Dto.ChargeRequest request = new PointV1Dto.ChargeRequest(1000L);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", "nonExistentUser");
            HttpEntity<PointV1Dto.ChargeRequest> requestEntity = new HttpEntity<>(request, headers);

            // act
            ResponseEntity<String> response = testRestTemplate.exchange(
                    ENDPOINT_CHARGE_POINT, HttpMethod.POST, requestEntity, String.class
            );

            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
