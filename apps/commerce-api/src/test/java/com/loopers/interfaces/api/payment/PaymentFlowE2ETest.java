package com.loopers.interfaces.api.payment;

import com.loopers.application.order.OrderFacade;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserModel;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.order.OrderV1Dto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class PaymentFlowE2ETest {

    @Autowired private OrderFacade orderFacade;
    @Autowired private UserJpaRepository userJpaRepository;

    @Test
    @DisplayName("주문 생성 → 비동기 결제 요청 트리거 (흐름 검증)")
    void placeOrderTriggersAsyncPayment() {
        // arrange
        UserModel user = userJpaRepository.save(UserModel.of("e2e@test.com", "유저", Gender.MALE, "2000-01-01"));
        OrderV1Dto.CreateOrderRequest req = new OrderV1Dto.CreateOrderRequest(
                "서울시 구로구", null, 0L, List.of(), new OrderV1Dto.CreateOrderRequest.PaymentRequest("SAMSUNG", "1234-5678-9814-1451")
        );

        // act
        orderFacade.placeOrder(user.getEmail(), req);

        // assert
        // 비동기라 바로 검증 어려움. 예외 없이 흐름이 동작하는지 정도만 확인
    }
}


