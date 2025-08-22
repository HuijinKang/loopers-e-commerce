package com.loopers.infrastructure.payment.feign.dto;

import java.util.List;

public class PgFeignDtos {
    public record ApiResponse<T>(Metadata meta, T data) {}
    public record Metadata(String result, String errorCode, String message) {}
    public record PaymentRequest(String orderId, CardTypeDto cardType, String cardNo, Long amount, String callbackUrl) {}
    public record TransactionResponse(String transactionKey, TransactionStatusResponse status, String reason) {}
    public record TransactionDetailResponse(String transactionKey, String orderId, CardTypeDto cardType, String cardNo, Long amount, TransactionStatusResponse status, String reason) {}
    public record OrderResponse(String orderId, List<TransactionResponse> transactions) {}
    public enum CardTypeDto { SAMSUNG, KB, HYUNDAI }
    public enum TransactionStatusResponse { PENDING, SUCCESS, FAILED }
}
