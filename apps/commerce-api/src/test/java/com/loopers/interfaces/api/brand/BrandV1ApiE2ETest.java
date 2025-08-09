package com.loopers.interfaces.api.brand;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandRepository;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BrandV1ApiE2ETest {

    private static final String ENDPOINT = "/api/v1/brands";

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private BrandRepository brandRepository;
    @Autowired private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() { databaseCleanUp.truncateAllTables(); }

    @DisplayName("GET /api/v1/brands/{brandId}")
    @Nested
    class GetBrand {

        @Test
        @DisplayName("브랜드 단건 조회 성공")
        void returnsBrand_whenExists() {
            // arrange
            BrandModel brand = brandRepository.save(BrandModel.of("무신사"));
            ParameterizedTypeReference<ApiResponse<BrandV1Dto.BrandResponse>> type = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<BrandV1Dto.BrandResponse>> response = restTemplate.exchange(
                    ENDPOINT + "/" + brand.getId(),
                    HttpMethod.GET,
                    null,
                    type
            );

            // assert
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().data().id()).isEqualTo(brand.getId()),
                    () -> assertThat(response.getBody().data().name()).isEqualTo("무신사")
            );
        }

        @Test
        @DisplayName("브랜드가 없으면 404 Not Found")
        void returnsNotFound_whenNotExists() {
            // arrange
            ParameterizedTypeReference<ApiResponse<BrandV1Dto.BrandResponse>> type = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<BrandV1Dto.BrandResponse>> response = restTemplate.exchange(
                    ENDPOINT + "/999999",
                    HttpMethod.GET,
                    null,
                    type
            );

            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}


