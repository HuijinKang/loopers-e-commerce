package com.loopers.application.payment.event;

import com.loopers.application.payment.PaymentResultApplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentResultEventListener {

    private final PaymentResultApplier paymentResultApplier;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentResultEvent event) {
        paymentResultApplier.apply(event.orderNo(), event.status());
    }
}
