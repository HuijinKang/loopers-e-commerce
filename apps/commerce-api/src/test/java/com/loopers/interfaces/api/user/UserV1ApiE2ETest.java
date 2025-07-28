package com.loopers.interfaces.api.user;

import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserModel;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserV1ApiE2ETest {

    private static final String ENDPOINT_CREATE = "/api/v1/users";
    private static final String ENDPOINT_GET = "/api/v1/users/me";

    private final TestRestTemplate restTemplate;
    private final UserJpaRepository userJpaRepository;
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public UserV1ApiE2ETest(
            TestRestTemplate restTemplate,
            UserJpaRepository userJpaRepository,
            DatabaseCleanUp databaseCleanUp
    ) {
        this.restTemplate = restTemplate;
        this.userJpaRepository = userJpaRepository;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("POST /api/v1/users")
    @Nested
    class Create {

        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsUserInfo_whenUserCreatedSuccessfully() {
            // arrange
            UserV1Dto.UserRequest request = new UserV1Dto.UserRequest(
                    "huijin123",
                    "희진",
                    Gender.MALE,
                    "1999-01-01",
                    "huijin123@example.com"
            );
            HttpEntity<UserV1Dto.UserRequest> httpEntity = new HttpEntity<>(request);
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response = restTemplate.exchange(
                    ENDPOINT_CREATE,
                    HttpMethod.POST,
                    httpEntity,
                    responseType
            );

            // assert
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo("huijin123"),
                    () -> assertThat(response.getBody().data().name()).isEqualTo("희진"),
                    () -> assertThat(response.getBody().data().gender()).isEqualTo(Gender.MALE),
                    () -> assertThat(response.getBody().data().birth()).isEqualTo("1999-01-01"),
                    () -> assertThat(response.getBody().data().email()).isEqualTo("huijin123@example.com")
            );
        }

        @DisplayName("회원 가입 시에 성별이 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void returnsBadRequest_whenGenderIsMissing() {
            // arrange
            UserV1Dto.UserRequest request = new UserV1Dto.UserRequest(
                    "huijin123",
                    "희진",
                    null,
                    "1999-01-01",
                    "huijin123@example.com"
            );
            HttpEntity<UserV1Dto.UserRequest> httpEntity = new HttpEntity<>(request);

            ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                    ENDPOINT_CREATE,
                    HttpMethod.POST,
                    httpEntity,
                    responseType
            );

            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("GET /api/v1/users/me")
    @Nested
    class Get {

        @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsUser_whenValidHeaderProvided() {
            // arrange
            UserModel user = new UserModel(
                    "huijin123",
                    "희진",
                    Gender.MALE,
                    "1999-01-01",
                    "huijin123@example.com"
            );
            userJpaRepository.save(user);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", "huijin123");

            HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response = restTemplate.exchange(
                    ENDPOINT_GET,
                    HttpMethod.GET,
                    httpEntity,
                    responseType
            );

            // assert
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo("huijin123"),
                    () -> assertThat(response.getBody().data().email()).isEqualTo("huijin123@example.com"),
                    () -> assertThat(response.getBody().data().name()).isEqualTo("희진"),
                    () -> assertThat(response.getBody().data().gender()).isEqualTo(Gender.MALE),
                    () -> assertThat(response.getBody().data().birth()).isEqualTo("1999-01-01")
            );
        }

        @DisplayName("존재하지 않는 ID 로 조회할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void returnsNotFound_whenUserNotExist() {
            // arrange
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", "none");

            HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response = restTemplate.exchange(
                    ENDPOINT_GET,
                    HttpMethod.GET,
                    httpEntity,
                    responseType
            );

            // assert
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }
}
