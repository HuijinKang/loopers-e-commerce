package com.loopers.domain.like;

import com.loopers.application.like.LikeFacade;
import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductStatus;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class LikeServiceIntegrationTest {

    @Autowired
    private LikeFacade likeFacade;

    @Autowired
    private LikeDomainService likeDomainService;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("좋아요를 누를 때,")
    @Nested
    class ToggleLike {

        @DisplayName("처음 누르면 좋아요가 등록된다.")
        @Test
        void likeProduct_whenFirstClick() {
            // arrange
            UserModel user = userRepository.save(UserModel.of(
                    "user1@example.com", "홍길동", Gender.MALE, "2000-01-01")
            );

            BrandModel brand = brandRepository.save(BrandModel.of("아디다스"));

            ProductModel product = productRepository.save(ProductModel.of(brand.getId(), "에어맥스", 120000L, 10, ProductStatus.ON_SALE));

            // act
            likeDomainService.toggleLike(user, product);

            // assert
            Optional<LikeModel> like = likeRepository.findByUserIdAndProductId(user.getId(), product.getId());
            assertThat(like).isPresent();
            assertThat(product.getLikeCount()).isEqualTo(1);
        }

        @DisplayName("이미 좋아요를 눌렀다면 다시 누르면 취소된다.")
        @Test
        void unlikeProduct_whenClickedAgain() {
            // arrange
            UserModel user = userRepository.save(UserModel.of(
                    "user1@example.com", "홍길동", Gender.MALE, "2000-01-01")
            );

            BrandModel brand = brandRepository.save(BrandModel.of("아디다스"));

            ProductModel product = productRepository.save(ProductModel.of(brand.getId(), "에어맥스", 120000L, 10, ProductStatus.ON_SALE));

            likeDomainService.toggleLike(user, product); // 처음 좋아요
            likeDomainService.toggleLike(user, product); // 다시 클릭 -> 취소

            // act
            Optional<LikeModel> like = likeRepository.findByUserIdAndProductId(user.getId(), product.getId());

            // assert
            assertThat(like).isEmpty();
            assertThat(product.getLikeCount()).isEqualTo(0);
        }

        @DisplayName("존재하지 않는 유저로 좋아요를 누르면 예외가 발생한다.")
        @Test
        void fails_whenUserNotFound() {
            // arrange
            BrandModel brand = brandRepository.save(BrandModel.of("아디다스"));

            ProductModel product = productRepository.save(ProductModel.of(brand.getId(), "에어맥스", 120000L, 10, ProductStatus.ON_SALE));

            UserModel fakeUser = UserModel.of("fake@example.com", "가짜", Gender.MALE, "1990-01-01");

            // act & assert
            assertThrows(CoreException.class,
                    () -> likeFacade.toggleLike(fakeUser.getEmail(), product.getId())
            );
        }
    }
}
