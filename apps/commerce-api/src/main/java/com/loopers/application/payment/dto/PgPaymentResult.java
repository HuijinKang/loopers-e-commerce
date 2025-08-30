package com.loopers.application.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PgPaymentResult {
    public enum Status { PENDING, SUCCESS, FAILED }

    private final String transactionKey;
    private final String orderId;
    private final String cardType;
    private final String cardNo;
    private final long amount;
    private final Status status;
    private final String reason;

    public static PgPaymentResult of(String transactionKey, String orderId, String cardType, String cardNo, long amount, Status status, String reason) {
        return new PgPaymentResult(transactionKey, orderId, cardType, cardNo, amount, status, reason);
    }
}
