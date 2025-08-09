package com.loopers.interfaces.api.like;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LikeV1ApiE2ETest {

    private static final String ENDPOINT = "/api/v1/like/products";

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() { databaseCleanUp.truncateAllTables(); }

    @DisplayName("좋아요 API")
    @Nested
    class LikeApi {

        @Test
        @DisplayName("좋아요 등록/취소 및 조회 성공")
        void like_unlike_and_check() {
            // arrange
            UserModel user = userJpaRepository.save(UserModel.of("like@test.com", "유저", Gender.MALE, "2000-01-01"));
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            ProductModel product = productRepository.save(ProductModel.of(brand.getId(), "상품", 1000L, 10));

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getEmail());

            // like
            ResponseEntity<ApiResponse<Void>> likeResponse = restTemplate.exchange(
                    ENDPOINT + "/" + product.getId(), HttpMethod.POST, new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {}
            );
            assertThat(likeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            // check liked
            ResponseEntity<ApiResponse<Boolean>> check1 = restTemplate.exchange(
                    ENDPOINT + "?productId=" + product.getId(), HttpMethod.GET, new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {}
            );
            assertThat(check1.getBody().data()).isTrue();

            // unlike
            ResponseEntity<ApiResponse<Void>> unlikeResponse = restTemplate.exchange(
                    ENDPOINT + "/" + product.getId(), HttpMethod.DELETE, new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {}
            );
            assertThat(unlikeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            // check unliked
            ResponseEntity<ApiResponse<Boolean>> check2 = restTemplate.exchange(
                    ENDPOINT + "?productId=" + product.getId(), HttpMethod.GET, new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {}
            );

            assertAll(
                    () -> assertThat(check2.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(check2.getBody().data()).isFalse()
            );
        }

        @Test
        @DisplayName("헤더 누락 시 400 반환")
        void returnsBadRequest_whenHeaderMissing() {
            // arrange
            // act
            ResponseEntity<String> response = restTemplate.exchange(
                    ENDPOINT + "/1", HttpMethod.POST, null, String.class
            );
            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("내가 좋아요한 상품 목록을 반환한다")
        void returnsMyLikedProducts() {
            // arrange
            UserModel user = userJpaRepository.save(UserModel.of("like-list@test.com", "유저", Gender.MALE, "2000-01-01"));
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            ProductModel p1 = productRepository.save(ProductModel.of(brand.getId(), "상품1", 1000L, 10));
            ProductModel p2 = productRepository.save(ProductModel.of(brand.getId(), "상품2", 1000L, 10));

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getEmail());

            // like p1, p2
            restTemplate.exchange(ENDPOINT + "/" + p1.getId(), HttpMethod.POST, new HttpEntity<>(headers), new ParameterizedTypeReference<ApiResponse<Void>>() {});
            restTemplate.exchange(ENDPOINT + "/" + p2.getId(), HttpMethod.POST, new HttpEntity<>(headers), new ParameterizedTypeReference<ApiResponse<Void>>() {});

            // act
            ResponseEntity<ApiResponse<List<Long>>> res = restTemplate.exchange(
                    ENDPOINT + "/list", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {}
            );

            // assert
            assertAll(
                    () -> assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(res.getBody().data()).containsExactlyInAnyOrder(p1.getId(), p2.getId())
            );
        }
    }
}
