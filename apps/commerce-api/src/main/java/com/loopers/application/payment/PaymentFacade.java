package com.loopers.application.payment;

import com.loopers.application.payment.dto.PgPaymentResult;
import com.loopers.domain.order.OrderDomainService;
import com.loopers.domain.order.OrderModel;
import com.loopers.domain.order.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentFacade {

    private final OrderDomainService orderDomainService;
    private final PgPaymentPort pgPaymentPort;

    @Transactional
    public void handleCallback(String orderId, PgPaymentResult.Status status) {
        OrderModel order = orderDomainService.getOrderByOrderNo(orderId);
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            return;
        }
        if (status == null || status == PgPaymentResult.Status.PENDING) {
            if (status == null) {
                log.warn("handleCallback ignored: null status for orderNo={}", orderId);
            }
        } else if (status == PgPaymentResult.Status.SUCCESS) {
            order.process();
        } else if (status == PgPaymentResult.Status.FAILED) {
            order.cancel();
        } else {
            log.warn("handleCallback ignored: unhandled status={} for orderNo={}", status, orderId);
        }
    }

    @Transactional
    public OrderStatus syncByOrderId(String orderId) {
        OrderModel order = orderDomainService.getOrderByOrderNo(orderId);
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            return order.getOrderStatus();
        }
        List<PgPaymentResult> results = pgPaymentPort.getPaymentsByOrderId(orderId);
        PgPaymentResult.Status finalStatus = results.stream()
                .sorted(Comparator.comparing(PgPaymentResult::getStatus))
                .map(PgPaymentResult::getStatus)
                .reduce((first, second) -> second)
                .orElse(PgPaymentResult.Status.PENDING);

        if (finalStatus == PgPaymentResult.Status.SUCCESS) {
            order.process();
        } else if (finalStatus == PgPaymentResult.Status.FAILED) {
            order.cancel();
        }
        return order.getOrderStatus();
    }
}
