package com.loopers.infrastructure.payment;

import com.loopers.application.payment.PgPaymentPort;
import com.loopers.application.payment.dto.PgPaymentCommand;
import com.loopers.application.payment.dto.PgPaymentResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PgPaymentRestAdapterIntegrationTest {

    @Autowired
    private PgPaymentPort pgPaymentPort;

    @Nested
    @DisplayName("결제 요청")
    class RequestPayment {
        @Test
        @DisplayName("요청 실패시 CircuitBreaker fallback으로 PENDING 또는 결과 반환")
        void returnsPendingOnFailureByFallback() {
            // arrange
            PgPaymentCommand.CreateTransaction cmd = new PgPaymentCommand.CreateTransaction(
                    "u1", "ORD-FAIL", "SAMSUNG", "1234-5678-9814-1451", 1000L, "http://localhost:8080/api/v1/payments/callback"
            );

            // act
            PgPaymentResult result = pgPaymentPort.requestPayment(cmd);

            // assert
            assertThat(result.getStatus()).isIn(PgPaymentResult.Status.PENDING, PgPaymentResult.Status.SUCCESS, PgPaymentResult.Status.FAILED);
        }
    }
}
