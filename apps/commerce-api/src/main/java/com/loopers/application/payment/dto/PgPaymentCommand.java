package com.loopers.application.payment.dto;

public class PgPaymentCommand {
    public record CreateTransaction(
            String userId,
            String orderId,
            String cardType, // SAMSUNG, KB, HYUNDAI
            String cardNo,   // xxxx-xxxx-xxxx-xxxx
            long amount,
            String callbackUrl
    ) {}
}
