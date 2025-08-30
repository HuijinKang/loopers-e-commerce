package com.loopers.application.order.event;

import com.loopers.application.payment.PgPaymentPort;
import com.loopers.application.payment.dto.PgPaymentCommand;
import com.loopers.application.payment.dto.PgPaymentResult;
import com.loopers.domain.order.OrderDomainService;
import com.loopers.domain.order.OrderModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPlacedEventListener {

    private final OrderDomainService orderDomainService;
    private final PgPaymentPort pgPaymentPort;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderPlacedEvent event) {
        try {
            OrderModel order = orderDomainService.getOrderByOrderNo(event.orderNo());
            long discounted = order.getDiscountedAmount();
            PgPaymentCommand.CreateTransaction pgCommand = PgPaymentCommand.CreateTransaction.of(
                    String.valueOf(event.userId()),
                    event.orderNo(),
                    event.cardType(),
                    event.cardNo(),
                    discounted,
                    "http://localhost:8080/api/v1/payments/callback"
            );
            PgPaymentResult result = pgPaymentPort.requestPayment(pgCommand);
            if (result == null || result.getTransactionKey() == null) {
                try { orderDomainService.cancelIfPending(event.orderNo()); } catch (Exception ignore) {}
            }
        } catch (Exception ex) {
            try { orderDomainService.cancelIfPending(event.orderNo()); } catch (Exception ignore) {}
            log.warn("OrderPlacedEvent handling failed for orderNo={}", event.orderNo(), ex);
        }
    }
}
