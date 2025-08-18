package com.loopers.interfaces.api.product;

import com.loopers.application.like.LikeFacade;
import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.ProductStatus;
import com.loopers.domain.product.ProductSortType;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductV1ApiE2ETest {

    private static final String ENDPOINT = "/api/v1/products";

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private BrandRepository brandRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private DatabaseCleanUp databaseCleanUp;
    @Autowired private LikeFacade likeFacade;

    @AfterEach
    void tearDown() { databaseCleanUp.truncateAllTables(); }

    @DisplayName("GET /api/v1/products")
    @Nested
    class GetProducts {

        @Test
        @DisplayName("최신순 기본 정렬로 상품 목록을 반환한다")
        void returnsLatestByDefault() {
            // arrange
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            productRepository.save(ProductModel.of(brand.getId(), "A", 1000L, 10, ProductStatus.ON_SALE));
            productRepository.save(ProductModel.of(brand.getId(), "B", 2000L, 10, ProductStatus.ON_SALE));

            ParameterizedTypeReference<ApiResponse<List<ProductV1Dto.ProductSummaryResponse>>> type = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<List<ProductV1Dto.ProductSummaryResponse>>> response = restTemplate.exchange(
                    ENDPOINT + "?page=0&size=10&sortType=" + ProductSortType.LATEST,
                    HttpMethod.GET,
                    null,
                    type
            );

            // assert
            assertAll(
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().data()).hasSize(2)
            );
        }

        @Test
        @DisplayName("status 필터를 적용하여 ON_SALE 상품만 반환한다")
        void filtersByStatus() {
            // arrange
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            productRepository.save(ProductModel.of(brand.getId(), "ON", 1000L, 10, ProductStatus.ON_SALE));

            ParameterizedTypeReference<ApiResponse<List<ProductV1Dto.ProductSummaryResponse>>> type = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<List<ProductV1Dto.ProductSummaryResponse>>> response = restTemplate.exchange(
                    ENDPOINT + "?page=0&size=10&sortType=" + ProductSortType.LATEST + "&status=ON_SALE",
                    HttpMethod.GET,
                    null,
                    type
            );

            // assert
            assertThat(response.getBody().data()).isNotEmpty();
        }

        @Test
        @DisplayName("잘못된 sortType 값이면 400 Bad Request를 반환한다")
        void returnsBadRequest_whenInvalidSortType() {
            // arrange
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            productRepository.save(ProductModel.of(brand.getId(), "A", 1000L, 10, ProductStatus.ON_SALE));

            // act
            ResponseEntity<String> response = restTemplate.exchange(
                    ENDPOINT + "?page=0&size=10&sortType=INVALID",
                    HttpMethod.GET,
                    null,
                    String.class
            );

            // assert
            assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        }

        @Test
        @DisplayName("가격 오름차순 정렬로 상품 목록을 반환한다")
        void returnsPriceAsc() {
            // arrange
            Long brandId = brandRepository.save(BrandModel.of("브랜드")).getId();
            productRepository.save(ProductModel.of(brandId, "A", 3000L, 10, ProductStatus.ON_SALE));
            productRepository.save(ProductModel.of(brandId, "B", 1000L, 10, ProductStatus.ON_SALE));
            productRepository.save(ProductModel.of(brandId, "C", 2000L, 10, ProductStatus.ON_SALE));

            ParameterizedTypeReference<ApiResponse<List<ProductV1Dto.ProductSummaryResponse>>> type = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<List<ProductV1Dto.ProductSummaryResponse>>> response = restTemplate.exchange(
                    ENDPOINT + "?page=0&size=10&sortType=" + ProductSortType.PRICE_ASC,
                    HttpMethod.GET,
                    null,
                    type
            );

            // assert
            List<ProductV1Dto.ProductSummaryResponse> data = response.getBody().data();
            assertAll(
                    () -> assertThat(data).hasSize(3),
                    () -> assertThat(data.get(0).price()).isEqualTo(1000L),
                    () -> assertThat(data.get(1).price()).isEqualTo(2000L),
                    () -> assertThat(data.get(2).price()).isEqualTo(3000L)
            );
        }

        @Test
        @DisplayName("좋아요 내림차순 정렬로 상품 목록을 반환한다")
        void returnsLikesDesc() {
            // arrange
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            ProductModel a = productRepository.save(ProductModel.of(brand.getId(), "A", 1000L, 10, ProductStatus.ON_SALE));
            ProductModel b = productRepository.save(ProductModel.of(brand.getId(), "B", 1000L, 10, ProductStatus.ON_SALE));

            // 서로 다른 유저 3명이 B를 좋아요, 1명이 A를 좋아요
            for (int i = 0; i < 4; i++) {
                UserModel user = userJpaRepository.save(UserModel.of("likes-"+i+"@t.com", "U"+i, com.loopers.domain.user.Gender.MALE, "1990-01-01"));
                if (i == 0) {
                    likeFacade.toggleLike(user.getEmail(), a.getId());
                }
                likeFacade.toggleLike(user.getEmail(), b.getId());
            }

            ParameterizedTypeReference<ApiResponse<List<ProductV1Dto.ProductSummaryResponse>>> type = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<List<ProductV1Dto.ProductSummaryResponse>>> response = restTemplate.exchange(
                    ENDPOINT + "?page=0&size=10&sortType=" + ProductSortType.LIKES_DESC,
                    HttpMethod.GET,
                    null,
                    type
            );

            // assert
            ApiResponse<List<ProductV1Dto.ProductSummaryResponse>> body = response.getBody();
            assertThat(body).isNotNull();
            List<ProductV1Dto.ProductSummaryResponse> data = body.data();
            assertAll(
                    () -> assertThat(data).isNotEmpty(),
                    () -> assertThat(data.get(0).id()).isEqualTo(b.getId()),
                    () -> assertThat(data.get(0).likeCount()).isGreaterThanOrEqualTo(data.get(1).likeCount())
            );
        }

        @Test
        @DisplayName("brandId 필터를 적용하면 해당 브랜드 상품만 반환한다")
        void filtersByBrandId() {
            // arrange
            BrandModel brand1 = brandRepository.save(BrandModel.of("브랜드-1"));
            BrandModel brand2 = brandRepository.save(BrandModel.of("브랜드-2"));
            productRepository.save(ProductModel.of(brand1.getId(), "A1", 1000L, 10, ProductStatus.ON_SALE));
            productRepository.save(ProductModel.of(brand1.getId(), "A2", 2000L, 10, ProductStatus.ON_SALE));
            productRepository.save(ProductModel.of(brand2.getId(), "B1", 3000L, 10, ProductStatus.ON_SALE));

            ParameterizedTypeReference<ApiResponse<List<ProductV1Dto.ProductSummaryResponse>>> type = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<List<ProductV1Dto.ProductSummaryResponse>>> response = restTemplate.exchange(
                    ENDPOINT + "?page=0&size=10&sortType=" + ProductSortType.LATEST + "&brandId=" + brand1.getId(),
                    HttpMethod.GET,
                    null,
                    type
            );

            // assert
            ApiResponse<List<ProductV1Dto.ProductSummaryResponse>> body = response.getBody();
            assertThat(body).isNotNull();
            List<ProductV1Dto.ProductSummaryResponse> data = body.data();
            assertAll(
                    () -> assertThat(data).hasSize(2),
                    () -> assertThat(data.stream().map(ProductV1Dto.ProductSummaryResponse::name).toList())
                            .containsExactlyInAnyOrder("A1", "A2")
            );
        }

        @Test
        @DisplayName("brandId + 좋아요 내림차순 정렬 시, 해당 브랜드 내에서만 정렬된다")
        void returnsLikesDescWithinBrand() {
            // arrange
            BrandModel brand1 = brandRepository.save(BrandModel.of("브랜드-1"));
            BrandModel brand2 = brandRepository.save(BrandModel.of("브랜드-2"));
            ProductModel a = productRepository.save(ProductModel.of(brand1.getId(), "A", 1000L, 10, ProductStatus.ON_SALE));
            ProductModel b = productRepository.save(ProductModel.of(brand1.getId(), "B", 1000L, 10, ProductStatus.ON_SALE));
            ProductModel c = productRepository.save(ProductModel.of(brand2.getId(), "C", 1000L, 10, ProductStatus.ON_SALE));

            // brand1 내부에서 B가 A보다 더 많은 좋아요를 갖도록 설정
            for (int i = 0; i < 4; i++) {
                UserModel user = userJpaRepository.save(UserModel.of("u-"+i+"@t.com", "U"+i, com.loopers.domain.user.Gender.MALE, "1990-01-01"));
                if (i < 1) likeFacade.toggleLike(user.getEmail(), a.getId());
                likeFacade.toggleLike(user.getEmail(), b.getId());
            }
            // 다른 브랜드의 C가 훨씬 많은 좋아요를 갖더라도 brand 필터에서 제외되어야 함
            for (int i = 0; i < 50; i++) {
                UserModel user = userJpaRepository.save(UserModel.of("c-"+i+"@t.com", "C"+i, com.loopers.domain.user.Gender.MALE, "1990-01-01"));
                likeFacade.toggleLike(user.getEmail(), c.getId());
            }

            ParameterizedTypeReference<ApiResponse<List<ProductV1Dto.ProductSummaryResponse>>> type = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<List<ProductV1Dto.ProductSummaryResponse>>> response = restTemplate.exchange(
                    ENDPOINT + "?page=0&size=10&sortType=" + ProductSortType.LIKES_DESC + "&brandId=" + brand1.getId(),
                    HttpMethod.GET,
                    null,
                    type
            );

            // assert
            ApiResponse<List<ProductV1Dto.ProductSummaryResponse>> body = response.getBody();
            assertThat(body).isNotNull();
            List<ProductV1Dto.ProductSummaryResponse> data = body.data();
            assertAll(
                    () -> assertThat(data).isNotEmpty(),
                    () -> assertThat(data.get(0).id()).isEqualTo(b.getId())
            );
        }
    }
}
