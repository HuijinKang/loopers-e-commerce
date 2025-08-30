package com.loopers.infrastructure.payment.feign;

import com.loopers.infrastructure.payment.feign.dto.PgFeignDtos.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "pgClient", url = "${pg.base-url}", configuration = PgFeignConfig.class)
public interface PgFeignClient {

    @PostMapping("/api/v1/payments")
    ApiResponse<TransactionResponse> request(
            @RequestHeader("X-USER-ID") String userId,
            @RequestBody PaymentRequest request
    );

    @GetMapping("/api/v1/payments/{transactionKey}")
    ApiResponse<TransactionDetailResponse> getTransaction(
            @RequestHeader("X-USER-ID") String userId,
            @PathVariable("transactionKey") String transactionKey
    );

    @GetMapping("/api/v1/payments")
    ApiResponse<OrderResponse> getTransactionsByOrder(
            @RequestHeader("X-USER-ID") String userId,
            @RequestParam("orderId") String orderId
    );
}
