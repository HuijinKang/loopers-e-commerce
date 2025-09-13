package com.loopers.application.payment.dto;

public class PgPaymentCommand {
    public record CreateTransaction(
            String userId,
            String orderId,
            String cardType, // SAMSUNG, KB, HYUNDAI
            String cardNo,   // xxxx-xxxx-xxxx-xxxx
            long amount,
            String callbackUrl
    ) {
        public static CreateTransaction of(String userId, String orderId, String cardType, String cardNo, long amount, String callbackUrl) {
            return new CreateTransaction(userId, orderId, cardType, cardNo, amount, callbackUrl);
        }
    }
}
