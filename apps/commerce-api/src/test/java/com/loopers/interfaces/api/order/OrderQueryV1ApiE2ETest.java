package com.loopers.interfaces.api.order;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.order.Option;
import com.loopers.domain.point.PointDomainService;
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
class OrderQueryV1ApiE2ETest {

    private static final String ENDPOINT = "/api/v1/orders";

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private PointDomainService pointDomainService;
    @Autowired private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() { databaseCleanUp.truncateAllTables(); }

    private Long createOrder(UserModel user, ProductModel product, long usePoint) {
        OrderV1Dto.CreateOrderRequest req = new OrderV1Dto.CreateOrderRequest(
                "서울시",
                null,
                usePoint,
                List.of(new OrderV1Dto.CreateOrderRequest.OrderItemRequest(
                        product.getId(), Option.of("BLK","L"), 1, product.getPrice()))
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-USER-ID", user.getEmail());
        HttpEntity<OrderV1Dto.CreateOrderRequest> entity = new HttpEntity<>(req, headers);
        ParameterizedTypeReference<ApiResponse<Long>> type = new ParameterizedTypeReference<>() {};
        ResponseEntity<ApiResponse<Long>> res = restTemplate.exchange(ENDPOINT, HttpMethod.POST, entity, type);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        return res.getBody().data();
    }

    @DisplayName("GET /api/v1/orders (목록)")
    @Nested
    class GetOrders {
        @Test
        @DisplayName("내 주문 목록을 반환한다")
        void returnsMyOrders() {
            // arrange
            UserModel user = userJpaRepository.save(UserModel.of("orders@test.com", "주문자", Gender.MALE, "1990-01-01"));
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            ProductModel p = productRepository.save(ProductModel.of(brand.getId(), "상품", 5000L, 10));
            pointDomainService.chargePoint(user, 50_000L);
            createOrder(user, p, 0L);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getEmail());
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ParameterizedTypeReference<ApiResponse<List<OrderV1Dto.OrderResponse>>> type = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<List<OrderV1Dto.OrderResponse>>> res = restTemplate.exchange(
                    ENDPOINT, HttpMethod.GET, entity, type
            );

            // assert
            assertAll(
                    () -> assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(res.getBody()).isNotNull(),
                    () -> assertThat(res.getBody().data()).isNotEmpty()
            );
        }
    }

    @DisplayName("GET /api/v1/orders/{orderId} (상세)")
    @Nested
    class GetOrderDetail {
        @Test
        @DisplayName("내 주문 상세를 반환한다")
        void returnsMyOrderDetail() {
            // arrange
            UserModel user = userJpaRepository.save(UserModel.of("order-detail@test.com", "주문자", Gender.MALE, "1990-01-01"));
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            ProductModel p = productRepository.save(ProductModel.of(brand.getId(), "상품", 7000L, 10));
            pointDomainService.chargePoint(user, 50_000L);
            Long orderId = createOrder(user, p, 0L);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getEmail());
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ParameterizedTypeReference<ApiResponse<OrderV1Dto.OrderResponse>> type = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<OrderV1Dto.OrderResponse>> res = restTemplate.exchange(
                    ENDPOINT + "/" + orderId, HttpMethod.GET, entity, type
            );

            // assert
            assertAll(
                    () -> assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(res.getBody()).isNotNull(),
                    () -> assertThat(res.getBody().data().id()).isEqualTo(orderId)
            );
        }

        @Test
        @DisplayName("다른 사람 주문 상세 조회 시 403 반환")
        void returnsForbidden_whenOtherUserOrder() {
            // arrange
            UserModel owner = userJpaRepository.save(UserModel.of("order-owner@test.com", "주문자", Gender.MALE, "1990-01-01"));
            UserModel other = userJpaRepository.save(UserModel.of("order-other@test.com", "타인", Gender.MALE, "1990-01-01"));
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            ProductModel p = productRepository.save(ProductModel.of(brand.getId(), "상품", 9000L, 10));
            pointDomainService.chargePoint(owner, 50_000L);
            Long orderId = createOrder(owner, p, 0L);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", other.getEmail());
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ParameterizedTypeReference<ApiResponse<OrderV1Dto.OrderResponse>> type = new ParameterizedTypeReference<>() {};

            // act
            ResponseEntity<ApiResponse<OrderV1Dto.OrderResponse>> res = restTemplate.exchange(
                    ENDPOINT + "/" + orderId, HttpMethod.GET, entity, type
            );

            // assert
            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }
    }
}
