package com.loopers.interfaces.api.user;

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
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        @DisplayName("회원가입 요청이 유효하면 성공 메시지를 반환한다.")
        @Test
        void createsUser_whenRequestIsValid() {
            // arrange
            UserV1Dto.UserRequest request = new UserV1Dto.UserRequest(
                    "chulsoo123",
                    "김철수",
                    "M",
                    "2000-01-01",
                    "chulsoo@example.com"
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
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(userJpaRepository.findByUserId("chulsoo123")).isPresent()
            );
        }
    }

    @DisplayName("GET /api/v1/users/me")
    @Nested
    class Get {
        @DisplayName("X-USER-ID 헤더로 유효한 ID를 주면 사용자 정보를 반환한다.")
        @Test
        void returnsUser_whenValidHeaderProvided() {
            // arrange
            UserModel user = new UserModel("younghee123", "김영희", "F", "1999-01-01", "younghee@example.com");
            userJpaRepository.save(user);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", "younghee123");

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
                    () -> assertThat(response.getBody().data().userId()).isEqualTo("younghee123"),
                    () -> assertThat(response.getBody().data().email()).isEqualTo("younghee@example.com")
            );
        }

        @DisplayName("존재하지 않는 ID를 주면 404 NOT_FOUND 응답을 반환한다.")
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

        @DisplayName("X-USER-ID 헤더가 없으면 400 BAD_REQUEST 응답을 반환한다.")
        @Test
        void returnsBadRequest_whenHeaderMissing() {
            // arrange
            HttpEntity<Void> httpEntity = new HttpEntity<>(new HttpHeaders());
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
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }
    }
}
