package com.loopers.application.product;

import com.loopers.application.like.LikeFacade;
import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.ProductSortType;
import com.loopers.domain.product.ProductStatus;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserModel;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.product.ProductV1Dto;
import com.loopers.testcontainers.RedisTestContainersConfig;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(RedisTestContainersConfig.class)
class ProductQueryFacadeCacheIntegrationTest {

    @Autowired private ProductQueryFacade productQueryFacade;
    @Autowired private LikeFacade likeFacade;
    @Autowired private ProductRepository productRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() { databaseCleanUp.truncateAllTables(); }

    @Test
    @DisplayName("상품 목록 캐시: 동일 파라미터 요청은 두 번째부터 캐시 히트")
    void listCacheHit() {
        // arrange
        BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
        for (int i = 0; i < 3; i++) {
            productRepository.save(ProductModel.of(brand.getId(), "P"+i, 1000L + i, 10, ProductStatus.ON_SALE));
        }

        // act
        List<ProductV1Dto.ProductSummaryResponse> first = productQueryFacade.getProducts(0, 10, ProductSortType.LATEST, ProductStatus.ON_SALE, brand.getId());
        List<ProductV1Dto.ProductSummaryResponse> second = productQueryFacade.getProducts(0, 10, ProductSortType.LATEST, ProductStatus.ON_SALE, brand.getId());

        // assert
        assertThat(first).hasSize(3);
        assertThat(second).hasSize(3);
    }

    @Test
    @DisplayName("상품 상세 캐시 무효화: 좋아요 토글 시 캐시 삭제 후 최신 likeCount 반환")
    void detailEvictOnLike() {
        // arrange
        BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
        ProductModel product = productRepository.save(ProductModel.of(brand.getId(), "P", 1000L, 10, ProductStatus.ON_SALE));
        UserModel user = userJpaRepository.save(UserModel.of("cache-like@test.com", "유저", Gender.MALE, "2000-01-01"));

        // act
        ProductV1Dto.ProductSummaryResponse before1 = productQueryFacade.getProduct(product.getId());
        ProductV1Dto.ProductSummaryResponse before2 = productQueryFacade.getProduct(product.getId());

        likeFacade.toggleLike(user.getEmail(), product.getId());

        ProductV1Dto.ProductSummaryResponse after = productQueryFacade.getProduct(product.getId());

        // assert
        assertThat(before1.likeCount()).isEqualTo(0);
        assertThat(before2.likeCount()).isEqualTo(0);
        assertThat(after.likeCount()).isEqualTo(1);
    }
}
