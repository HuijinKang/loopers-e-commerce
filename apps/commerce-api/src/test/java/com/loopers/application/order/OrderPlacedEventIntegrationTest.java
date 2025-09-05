package com.loopers.application.order;

import com.loopers.application.payment.PgPaymentPort;
import com.loopers.domain.product.ProductStatus;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserModel;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.order.OrderV1Dto;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest
class OrderPlacedEventIntegrationTest {

    @Autowired private OrderFacade orderFacade;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private com.loopers.domain.brand.BrandRepository brandRepository;
    @Autowired private com.loopers.domain.product.ProductRepository productRepository;
    @Autowired private DatabaseCleanUp databaseCleanUp;

    @MockitoBean
    private PgPaymentPort pgPaymentPort;

    @AfterEach
    void tearDown() { databaseCleanUp.truncateAllTables(); }

    @DisplayName("주문 생성 시 AFTER_COMMIT 이벤트로 PG 결제 요청이 비동기로 호출된다")
    @Test
    void placeOrder_triggers_async_payment_request() throws InterruptedException {
        // arrange
        UserModel user = userJpaRepository.save(UserModel.of("huijin123@example.com", "강희진", Gender.MALE, "2000-01-01"));
        var brand = brandRepository.save(com.loopers.domain.brand.BrandModel.of("브랜드"));
        var product = productRepository.save(com.loopers.domain.product.ProductModel.of(brand.getId(), "상품1", 5000L, 10, ProductStatus.ON_SALE));

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(inv -> {
            latch.countDown();
            return com.loopers.application.payment.dto.PgPaymentResult.of(
                    "T-TEST", "ORD-X", "SAMSUNG", "xxxx", 1000,
                    com.loopers.application.payment.dto.PgPaymentResult.Status.PENDING, null);
        }).when(pgPaymentPort).requestPayment(any());

        OrderV1Dto.CreateOrderRequest req = new OrderV1Dto.CreateOrderRequest(
                "서울시 구로구",
                null,
                0L,
                List.of(new OrderV1Dto.CreateOrderRequest.OrderItemRequest(
                        product.getId(), com.loopers.domain.order.Option.of("RED","M"), 1, product.getPrice()
                )),
                new OrderV1Dto.CreateOrderRequest.PaymentRequest("SAMSUNG", "1234-5678-9814-1451")
        );

        // act
        orderFacade.placeOrder(user.getEmail(), req);

        // assert
        boolean called = latch.await(10, TimeUnit.SECONDS);
        assertThat(called).isTrue();
        Mockito.verify(pgPaymentPort, Mockito.atLeastOnce()).requestPayment(any());
    }
}
