package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.dto.PgPaymentResult;

public class PaymentCallbackDto {

    public record CallbackRequest(
            String transactionKey,
            String orderId,
            String cardType,
            String cardNo,
            long amount,
            PgPaymentResult.Status status,
            String reason
    ) {}
}
