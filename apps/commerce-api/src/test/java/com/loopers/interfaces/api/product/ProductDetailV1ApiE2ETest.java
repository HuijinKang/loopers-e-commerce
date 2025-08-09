package com.loopers.interfaces.api.product;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductRepository;
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
class ProductDetailV1ApiE2ETest {

    private static final String ENDPOINT = "/api/v1/products";

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private BrandRepository brandRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() { databaseCleanUp.truncateAllTables(); }

    @DisplayName("GET /api/v1/products/{productId}")
    @Nested
    class GetProductDetail {

        @Test
        @DisplayName("상품 상세 조회 성공")
        void returnsProduct_whenExists() {
            // arrange
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            ProductModel product = productRepository.save(ProductModel.of(brand.getId(), "스니커즈", 120000L, 10));
            ParameterizedTypeReference<ApiResponse<ProductV1Dto.ProductSummaryResponse>> type = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<ProductV1Dto.ProductSummaryResponse>> response = restTemplate.exchange(
                    ENDPOINT + "/" + product.getId(),
                    HttpMethod.GET,
                    null,
                    type
            );

            // assert
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().data().id()).isEqualTo(product.getId()),
                    () -> assertThat(response.getBody().data().name()).isEqualTo("스니커즈")
            );
        }

        @Test
        @DisplayName("상품이 없으면 404 Not Found")
        void returnsNotFound_whenNotExists() {
            // arrange
            ParameterizedTypeReference<ApiResponse<ProductV1Dto.ProductSummaryResponse>> type = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<ProductV1Dto.ProductSummaryResponse>> response = restTemplate.exchange(
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
