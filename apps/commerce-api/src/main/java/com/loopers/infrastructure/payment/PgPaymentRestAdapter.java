package com.loopers.infrastructure.payment;

import com.loopers.application.payment.PgPaymentPort;
import com.loopers.application.payment.dto.PgPaymentCommand;
import com.loopers.application.payment.dto.PgPaymentResult;
import com.loopers.infrastructure.payment.feign.PgFeignClient;
import com.loopers.infrastructure.payment.feign.dto.PgFeignDtos;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableConfigurationProperties(PgPaymentProperties.class)
public class PgPaymentRestAdapter implements PgPaymentPort {

    private final PgFeignClient feign;

    public PgPaymentRestAdapter(PgFeignClient feign) {
        this.feign = feign;
    }

    @Override
    @CircuitBreaker(name = "pg", fallbackMethod = "requestFallback")
    public PgPaymentResult requestPayment(PgPaymentCommand.CreateTransaction command) {
        PgFeignDtos.PaymentRequest req = new PgFeignDtos.PaymentRequest(
                command.orderId(), PgFeignDtos.CardTypeDto.valueOf(command.cardType()), command.cardNo(), command.amount(), command.callbackUrl()
        );
        PgFeignDtos.ApiResponse<PgFeignDtos.TransactionResponse> resp = feign.request(command.userId(), req);
        PgFeignDtos.TransactionResponse tr = resp.data();
        return PgPaymentResult.of(tr.transactionKey(), command.orderId(), command.cardType(), command.cardNo(), command.amount(), mapStatus(tr.status()), tr.reason());
    }

    @Override
    @CircuitBreaker(name = "pg", fallbackMethod = "getByTransactionFallback")
    public PgPaymentResult getPaymentByTransactionKey(String transactionKey) {
        PgFeignDtos.ApiResponse<PgFeignDtos.TransactionDetailResponse> resp = feign.getTransaction("system", transactionKey);
        PgFeignDtos.TransactionDetailResponse d = resp.data();
        return PgPaymentResult.of(d.transactionKey(), d.orderId(), d.cardType().name(), d.cardNo(), d.amount(), mapStatus(d.status()), d.reason());
    }

    @Override
    @CircuitBreaker(name = "pg", fallbackMethod = "getByOrderFallback")
    public List<PgPaymentResult> getPaymentsByOrderId(String orderId) {
        PgFeignDtos.ApiResponse<PgFeignDtos.OrderResponse> resp = feign.getTransactionsByOrder("system", orderId);
        return resp.data().transactions().stream()
                .map(t -> PgPaymentResult.of(t.transactionKey(), orderId, null, null, 0L, mapStatus(t.status()), t.reason()))
                .toList();
    }

    private PgPaymentResult.Status mapStatus(PgFeignDtos.TransactionStatusResponse s) {
        return switch (s) {
            case PENDING -> PgPaymentResult.Status.PENDING;
            case SUCCESS -> PgPaymentResult.Status.SUCCESS;
            case FAILED -> PgPaymentResult.Status.FAILED;
        };
    }

    private PgPaymentResult requestFallback(PgPaymentCommand.CreateTransaction command, Throwable t) {
        return PgPaymentResult.of(null, command.orderId(), command.cardType(), command.cardNo(), command.amount(), PgPaymentResult.Status.PENDING, "fallback: " + t.getClass().getSimpleName());
    }

    private PgPaymentResult getByTransactionFallback(String transactionKey, Throwable t) {
        return PgPaymentResult.of(transactionKey, null, null, null, 0L, PgPaymentResult.Status.PENDING, "fallback: " + t.getClass().getSimpleName());
    }

    private java.util.List<PgPaymentResult> getByOrderFallback(String orderId, Throwable t) {
        return java.util.Collections.emptyList();
    }
}
