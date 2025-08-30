package com.loopers.interfaces.api.payment;

public class PaymentSyncDto {

    public record SyncResponse(String orderId, String status) {}
}
