package com.loopers.domain.like;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LikeModelTest {

    @DisplayName("LikeModel을 생성할 때,")
    @Nested
    class Create {

        @DisplayName("정적 팩토리 메서드로 생성하면 유저와 상품이 정상 연결된다")
        @Test
        void createLikeWithStaticFactory() {
            // arrange
            UserModel user = UserModel.of(
                    "huijin123@example.com",
                    "강희진",
                    Gender.MALE,
                    "2000-01-01"
            );

            BrandModel brand = BrandModel.of("나이키");

            ProductModel product = ProductModel.of(
                    brand.getId(),
                    "에어포스",
                    150000L,
                    10
            );

            // act
            LikeModel like = LikeModel.of(user.getId(), product.getId());

            // assert
            assertThat(like.getUserId()).isEqualTo(user.getId());
            assertThat(like.getProductId()).isEqualTo(product.getId());
        }
    }
}
