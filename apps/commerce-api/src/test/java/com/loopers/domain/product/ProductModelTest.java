package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductModelTest {

    @DisplayName("상품 생성 시")
    @Nested
    class Create {

        @DisplayName("정상적인 값이 주어지면 상품이 생성된다")
        @Test
        void createsProduct_whenValidInputProvided() {
            // arrange
            String name = "상품";
            long price = 1000L;
            int stock = 10;

            // act
            ProductModel product = ProductModel.of(1L, name, price, stock, ProductStatus.ON_SALE);

            // assert
            assertAll(
                    () -> assertThat(product.getName()).isEqualTo(name),
                    () -> assertThat(product.getPrice()).isEqualTo(price),
                    () -> assertThat(product.getStock()).isEqualTo(stock),
                    () -> assertThat(product.getBrandId()).isEqualTo(1L)
            );
        }

        @DisplayName("가격이 음수면 예외가 발생한다")
        @Test
        void throwsException_whenPriceIsNegative() {
            // arrange
            String name = "상품";
            long price = -100L;
            int stock = 5;

            // act & assert
            CoreException exception = assertThrows(CoreException.class, () ->
                    ProductModel.of(1L, name, price, stock, ProductStatus.ON_SALE)
            );
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("재고가 음수면 예외가 발생한다")
        @Test
        void throwsException_whenStockIsNegative() {
            // arrange
            String name = "상품";
            long price = 1000L;
            int stock = -1;

            // act & assert
            CoreException exception = assertThrows(CoreException.class, () ->
                    ProductModel.of(1L, name, price, stock, ProductStatus.ON_SALE)
            );
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("재고 관련 기능은")
    @Nested
    class Stock {

        @DisplayName("재고가 감소하면 기존 재고에서 차감된다")
        @Test
        void decreasesStock_whenSufficientQuantity() {
            // arrange
            ProductModel product = ProductModel.of(1L, "상품", 1000L, 10, ProductStatus.ON_SALE);

            // act
            product.decreaseStock(3);

            // assert
            assertThat(product.getStock()).isEqualTo(7);
        }

        @DisplayName("재고 차감 시 부족하면 예외가 발생한다")
        @Test
        void throwsException_whenStockIsInsufficient() {
            // arrange
            ProductModel product = ProductModel.of(1L, "상품", 1000L, 2, ProductStatus.ON_SALE);

            // act & assert
            CoreException exception = assertThrows(CoreException.class, () ->
                    product.decreaseStock(5)
            );
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("좋아요 기능은")
    @Nested
    class LikeCount {

        @DisplayName("좋아요 수가 증가하면 1만큼 증가한다")
        @Test
        void increasesLikeCount_whenCalled() {
            // arrange
            ProductModel product = ProductModel.of(1L, "상품", 1000L, 10, ProductStatus.ON_SALE);

            // act
            product.increaseLikeCount();

            // assert
            assertThat(product.getLikeCount()).isEqualTo(1);
        }

        @DisplayName("좋아요 수가 감소하면 1만큼 감소한다")
        @Test
        void decreasesLikeCount_whenGreaterThanZero() {
            // arrange
            ProductModel product = ProductModel.of(1L, "상품", 1000L, 10, ProductStatus.ON_SALE);
            product.increaseLikeCount();

            // act
            product.decreaseLikeCount();

            // assert
            assertThat(product.getLikeCount()).isEqualTo(0);
        }

        @DisplayName("좋아요 수가 0일 때 감소하면 0을 유지한다")
        @Test
        void likeCountDoesNotGoBelowZero() {
            // arrange
            ProductModel product = ProductModel.of(1L, "상품", 1000L, 10, ProductStatus.ON_SALE);

            // act
            product.decreaseLikeCount();

            // assert
            assertThat(product.getLikeCount()).isEqualTo(0);
        }
    }


    @DisplayName("상품의 판매 가능 여부는")
    @Nested
    class Availability {

        @DisplayName("재고가 있고 상태가 ON_SALE이면 true이다")
        @Test
        void isAvailable_whenInStockAndOnSale() {
            // arrange
            ProductModel product = ProductModel.of(1L, "상품", 1000L, 5, ProductStatus.ON_SALE);

            // act
            boolean available = product.isAvailable();

            // assert
            assertThat(available).isTrue();
        }

        @DisplayName("재고가 0이면 false이다")
        @Test
        void isNotAvailable_whenStockIsZero() {
            // arrange
            ProductModel product = ProductModel.of(1L, "상품", 1000L, 0, ProductStatus.ON_SALE);

            // act
            boolean available = product.isAvailable();

            // assert
            assertThat(available).isFalse();
        }
    }
}
