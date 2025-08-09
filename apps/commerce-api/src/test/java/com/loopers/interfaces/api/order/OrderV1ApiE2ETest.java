package com.loopers.interfaces.api.order;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.coupon.*;
import com.loopers.domain.point.PointDomainService;
import com.loopers.domain.order.Option;
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
class OrderV1ApiE2ETest {

    private static final String ENDPOINT = "/api/v1/orders";

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private BrandRepository brandRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CouponRepository couponRepository;
    @Autowired private IssuedCouponRepository issuedCouponRepository;
    @Autowired private PointDomainService pointDomainService;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() { databaseCleanUp.truncateAllTables(); }

    @DisplayName("POST /api/v1/orders")
    @Nested
    class CreateOrder {

        @DisplayName("성공 시 주문 ID 를 반환한다")
        @Test
        void returnsOrderId_whenSuccess() {
            // arrange
            UserModel user = userJpaRepository.save(UserModel.of("order@test.com", "주문자", Gender.MALE, "1990-01-01"));
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            ProductModel product = productRepository.save(ProductModel.of(brand.getId(), "상품", 10000L, 10));

            pointDomainService.chargePoint(user, 50000L);

            OrderV1Dto.CreateOrderRequest request = new OrderV1Dto.CreateOrderRequest(
                    "서울시 강남구",
                    null,
                    0L,
                    List.of(new OrderV1Dto.CreateOrderRequest.OrderItemRequest(product.getId(), Option.of("RED","M"), 1, 10000L))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getEmail());
            HttpEntity<OrderV1Dto.CreateOrderRequest> entity = new HttpEntity<>(request, headers);
            ParameterizedTypeReference<ApiResponse<Long>> responseType = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<Long>> response = restTemplate.exchange(
                    ENDPOINT, HttpMethod.POST, entity, responseType
            );

            // assert
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().data()).isNotNull()
            );
        }

        @DisplayName("존재하지 않는 쿠폰으로 주문하면 404를 반환한다")
        @Test
        void returnsNotFound_whenIssuedCouponNotExist() {
            // arrange
            UserModel user = userJpaRepository.save(UserModel.of("nofc@test.com", "주문자", Gender.MALE, "1990-01-01"));
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            ProductModel product = productRepository.save(ProductModel.of(brand.getId(), "상품", 10000L, 5));

            pointDomainService.chargePoint(user, 50000L);

            OrderV1Dto.CreateOrderRequest request = new OrderV1Dto.CreateOrderRequest(
                    "서울시",
                    9_999_999L, // 존재하지 않는 발급 쿠폰 ID
                    0L,
                    List.of(new OrderV1Dto.CreateOrderRequest.OrderItemRequest(product.getId(), Option.of("BLACK","L"), 1, 10000L))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getEmail());
            HttpEntity<OrderV1Dto.CreateOrderRequest> entity = new HttpEntity<>(request, headers);

            ParameterizedTypeReference<ApiResponse<Long>> type = new ParameterizedTypeReference<>() {};
            // act
            ResponseEntity<ApiResponse<Long>> response = restTemplate.exchange(ENDPOINT, HttpMethod.POST, entity, type);
            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @DisplayName("이미 사용된 쿠폰으로 주문하면 400을 반환한다")
        @Test
        void returnsBadRequest_whenCouponUsed() {
            // arrange
            UserModel user = userJpaRepository.save(UserModel.of("usedc@test.com", "주문자", Gender.MALE, "1990-01-01"));
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            ProductModel product = productRepository.save(ProductModel.of(brand.getId(), "상품", 10000L, 5));

            pointDomainService.chargePoint(user, 50000L);

            CouponModel coupon = couponRepository.save(CouponModel.ofFixed("FIX-1", "fixed", 1000L));
            IssuedCouponModel issued = issuedCouponRepository.save(IssuedCouponModel.of(coupon, user.getId(), IssuedCouponStatus.USED));

            OrderV1Dto.CreateOrderRequest request = new OrderV1Dto.CreateOrderRequest(
                    "서울시",
                    issued.getId(),
                    0L,
                    List.of(new OrderV1Dto.CreateOrderRequest.OrderItemRequest(product.getId(), Option.of("BLACK","L"), 1, 10000L))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getEmail());
            HttpEntity<OrderV1Dto.CreateOrderRequest> entity = new HttpEntity<>(request, headers);

            ParameterizedTypeReference<ApiResponse<Long>> type = new ParameterizedTypeReference<>() {};
            // act
            ResponseEntity<ApiResponse<Long>> response = restTemplate.exchange(ENDPOINT, HttpMethod.POST, entity, type);
            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @DisplayName("쿠폰 소유자가 아니면 403을 반환한다")
        @Test
        void returnsForbidden_whenCouponNotOwnedByUser() {
            // arrange
            UserModel owner = userJpaRepository.save(UserModel.of("owner@test.com", "소유자", Gender.MALE, "1990-01-01"));
            UserModel other = userJpaRepository.save(UserModel.of("other@test.com", "타인", Gender.MALE, "1990-01-01"));
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            ProductModel product = productRepository.save(ProductModel.of(brand.getId(), "상품", 10000L, 5));

            pointDomainService.chargePoint(other, 50000L);

            CouponModel coupon = couponRepository.save(CouponModel.ofFixed("FIX-OWN", "fixed", 1000L));
            IssuedCouponModel issued = issuedCouponRepository.save(IssuedCouponModel.of(coupon, owner.getId(), IssuedCouponStatus.ISSUED));

            OrderV1Dto.CreateOrderRequest request = new OrderV1Dto.CreateOrderRequest(
                    "서울시",
                    issued.getId(),
                    0L,
                    List.of(new OrderV1Dto.CreateOrderRequest.OrderItemRequest(product.getId(), Option.of("BLACK","L"), 1, 10000L))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", other.getEmail());
            HttpEntity<OrderV1Dto.CreateOrderRequest> entity = new HttpEntity<>(request, headers);

            ParameterizedTypeReference<ApiResponse<Long>> type = new ParameterizedTypeReference<>() {};
            // act
            ResponseEntity<ApiResponse<Long>> response = restTemplate.exchange(ENDPOINT, HttpMethod.POST, entity, type);
            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        @DisplayName("포인트가 부족하면 400을 반환한다(포인트 레코드는 존재)")
        @Test
        void returnsBadRequest_whenPointInsufficientWithExistingRecord() {
            // arrange
            UserModel user = userJpaRepository.save(UserModel.of("insufficient@test.com", "주문자", Gender.MALE, "1990-01-01"));
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            ProductModel product = productRepository.save(ProductModel.of(brand.getId(), "상품", 10000L, 5));

            pointDomainService.chargePoint(user, 500L); // 레코드 생성 + 소액만 충전

            OrderV1Dto.CreateOrderRequest request = new OrderV1Dto.CreateOrderRequest(
                    "서울시",
                    null,
                    1_000L, // 보유보다 큰 사용 포인트
                    List.of(new OrderV1Dto.CreateOrderRequest.OrderItemRequest(product.getId(), Option.of("BLACK","L"), 1, 10000L))
            );
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getEmail());
            HttpEntity<OrderV1Dto.CreateOrderRequest> entity = new HttpEntity<>(request, headers);

            ParameterizedTypeReference<ApiResponse<Long>> type = new ParameterizedTypeReference<>() {};
            // act
            ResponseEntity<ApiResponse<Long>> response = restTemplate.exchange(ENDPOINT, HttpMethod.POST, entity, type);
            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @DisplayName("재고가 부족하면 400을 반환한다")
        @Test
        void returnsBadRequest_whenStockInsufficient() {
            // arrange
            UserModel user = userJpaRepository.save(UserModel.of("stock@test.com", "주문자", Gender.MALE, "1990-01-01"));
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            ProductModel product = productRepository.save(ProductModel.of(brand.getId(), "상품", 10000L, 1));
            pointDomainService.chargePoint(user, 50000L);

            OrderV1Dto.CreateOrderRequest request = new OrderV1Dto.CreateOrderRequest(
                    "서울시",
                    null,
                    0L,
                    List.of(new OrderV1Dto.CreateOrderRequest.OrderItemRequest(product.getId(), Option.of("BLACK","L"), 2, 10000L))
            );
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getEmail());
            HttpEntity<OrderV1Dto.CreateOrderRequest> entity = new HttpEntity<>(request, headers);

            ParameterizedTypeReference<ApiResponse<Long>> type = new ParameterizedTypeReference<>() {};
            // act
            ResponseEntity<ApiResponse<Long>> response = restTemplate.exchange(ENDPOINT, HttpMethod.POST, entity, type);
            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @DisplayName("보유 포인트가 없는데 포인트 사용 시 404를 반환한다")
        @Test
        void returnsNotFound_whenPointRecordAbsent() {
            // arrange
            UserModel user = userJpaRepository.save(UserModel.of("point@test.com", "주문자", Gender.MALE, "1990-01-01"));
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            ProductModel product = productRepository.save(ProductModel.of(brand.getId(), "상품", 10000L, 5));

            // 보유 포인트 0 상태에서 1000 사용 시도
            OrderV1Dto.CreateOrderRequest request = new OrderV1Dto.CreateOrderRequest(
                    "서울시",
                    null,
                    1000L,
                    List.of(new OrderV1Dto.CreateOrderRequest.OrderItemRequest(product.getId(), Option.of("BLACK","L"), 1, 10000L))
            );
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getEmail());
            HttpEntity<OrderV1Dto.CreateOrderRequest> entity = new HttpEntity<>(request, headers);

            ParameterizedTypeReference<ApiResponse<Long>> type = new ParameterizedTypeReference<>() {};
            // act
            ResponseEntity<ApiResponse<Long>> response = restTemplate.exchange(ENDPOINT, HttpMethod.POST, entity, type);
            // assert (포인트 레코드 미존재로 404)
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @DisplayName("X-USER-ID 헤더가 없으면 400을 반환한다")
        @Test
        void returnsBadRequest_whenHeaderMissing() {
            // arrange
            OrderV1Dto.CreateOrderRequest request = new OrderV1Dto.CreateOrderRequest(
                    "서울시",
                    null,
                    0L,
                    List.of()
            );

            HttpEntity<OrderV1Dto.CreateOrderRequest> entity = new HttpEntity<>(request);
            ParameterizedTypeReference<ApiResponse<Long>> type = new ParameterizedTypeReference<>() {};
            // act
            ResponseEntity<ApiResponse<Long>> response = restTemplate.exchange(ENDPOINT, HttpMethod.POST, entity, type);
            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}
