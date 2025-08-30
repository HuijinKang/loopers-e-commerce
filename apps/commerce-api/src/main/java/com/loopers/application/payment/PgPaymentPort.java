package com.loopers.application.payment;

import com.loopers.application.payment.dto.PgPaymentCommand;
import com.loopers.application.payment.dto.PgPaymentResult;

import java.util.List;

public interface PgPaymentPort {

    PgPaymentResult requestPayment(PgPaymentCommand.CreateTransaction command);

    PgPaymentResult getPaymentByTransactionKey(String transactionKey);

    List<PgPaymentResult> getPaymentsByOrderId(String orderId);
}
