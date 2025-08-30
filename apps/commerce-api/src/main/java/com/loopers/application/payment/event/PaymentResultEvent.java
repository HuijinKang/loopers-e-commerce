package com.loopers.application.payment.event;

import com.loopers.application.payment.dto.PgPaymentResult;

public record PaymentResultEvent(
        String orderNo,
        PgPaymentResult.Status status
) {
    public static PaymentResultEvent of(String orderNo, PgPaymentResult.Status status) {
        return new PaymentResultEvent(orderNo, status);
    }
}
