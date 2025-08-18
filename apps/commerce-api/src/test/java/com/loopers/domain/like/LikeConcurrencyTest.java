package com.loopers.domain.like;

import com.loopers.application.like.LikeFacade;
import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductStatus;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserModel;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LikeConcurrencyTest {

    @Autowired private LikeFacade likeFacade;
    @Autowired private BrandRepository brandRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private LikeRepository likeRepository;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() { databaseCleanUp.truncateAllTables(); }

    @DisplayName("50명이 동시에 좋아요를 눌러도 총 좋아요 수와 like 테이블 상태가 일치한다")
    @Test
    void likeCountAccurate_whenConcurrentLikes() throws InterruptedException {
        // arrange
        BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
        ProductModel product = productRepository.save(ProductModel.of(brand.getId(), "상품", 10_000L, 10, ProductStatus.ON_SALE));
        List<UserModel> users = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            users.add(userJpaRepository.save(UserModel.of("like-"+i+"@test.com", "U"+i, Gender.MALE, "1990-01-01")));
        }

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(50);

        // act
        for (int i = 0; i < 50; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    likeFacade.toggleLike(users.get(idx).getEmail(), product.getId());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        // assert
        long likedRows = users.stream()
                .filter(u -> likeRepository.existsByUserIdAndProductId(u.getId(), product.getId()))
                .count();
        assertThat(likedRows).isEqualTo(50);

        ProductModel refreshed = productRepository.findById(product.getId()).orElseThrow();
        assertThat(refreshed.getLikeCount()).isEqualTo(50);
    }
}
