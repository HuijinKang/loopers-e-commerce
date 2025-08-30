package com.loopers.application.like;

import com.loopers.application.product.cache.ProductCachePort;
import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.ProductStatus;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserModel;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.verify;

@SpringBootTest
class LikeFacadeIntegrationTest {

    @Autowired private LikeFacade likeFacade;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private DatabaseCleanUp databaseCleanUp;

    @MockitoBean
    private ProductCachePort productCachePort;

    @AfterEach
    void tearDown() { databaseCleanUp.truncateAllTables(); }

    @Test
    @DisplayName("좋아요 토글 시 상세 캐시가 무효화된다")
    void evicts_detail_cache_on_toggle() {
        // arrange
        UserModel user = userJpaRepository.save(UserModel.of("huijin123@example.com", "강희진", Gender.MALE, "2000-01-01"));
        BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
        ProductModel product = productRepository.save(ProductModel.of(brand.getId(), "상품1", 5000L, 10, ProductStatus.ON_SALE));

        // act
        likeFacade.toggleLike(user.getEmail(), product.getId());

        // assert
        verify(productCachePort).evict("product:detail:" + product.getId());
    }
}
