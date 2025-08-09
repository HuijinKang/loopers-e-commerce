package com.loopers.domain.brand;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BrandModelTest {

    @DisplayName("브랜드 생성 시")
    @Nested
    class Create {

        @DisplayName("이름이 주어지면 활성화 상태로 생성된다")
        @Test
        void createsBrandWithActiveStatus() {
            // arrange
            String name = "나이키";

            // act
            BrandModel brand = BrandModel.of(name);

            // assert
            assertThat(brand.getName()).isEqualTo(name);
            assertThat(brand.getIsActive()).isTrue();
        }
    }

    @DisplayName("활성/비활성 전환")
    @Nested
    class Activation {

        @DisplayName("비활성화 메서드를 호출하면 isActive가 false가 된다")
        @Test
        void deactivatesBrand() {
            // arrange
            BrandModel brand = BrandModel.of("아디다스");

            // act
            brand.deactivate();

            // assert
            assertThat(brand.getIsActive()).isFalse();
        }

        @DisplayName("활성화 메서드를 호출하면 isActive가 true가 된다")
        @Test
        void activatesBrand() {
            // arrange
            BrandModel brand = BrandModel.of("아디다스");
            brand.deactivate();

            // act
            brand.activate();

            // assert
            assertThat(brand.getIsActive()).isTrue();
        }
    }
}
