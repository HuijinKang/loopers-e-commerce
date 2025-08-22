package com.loopers.application.payment;

import com.loopers.application.payment.dto.PgPaymentResult;
import com.loopers.domain.order.OrderDomainService;
import com.loopers.domain.order.OrderModel;
import com.loopers.domain.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final OrderDomainService orderDomainService;
    private final com.loopers.application.payment.PgPaymentPort pgPaymentPort;

    @Transactional
    public void handleCallback(String orderId, PgPaymentResult.Status status) {
        OrderModel order = orderDomainService.getOrderByOrderNo(orderId);
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            return;
        }
        if (status == PgPaymentResult.Status.SUCCESS) {
            order.updateOrderStatus(OrderStatus.PROCESSING);
        } else if (status == PgPaymentResult.Status.FAILED) {
            order.updateOrderStatus(OrderStatus.CANCELED);
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
            order.updateOrderStatus(OrderStatus.PROCESSING);
        } else if (finalStatus == PgPaymentResult.Status.FAILED) {
            order.updateOrderStatus(OrderStatus.CANCELED);
        }
        return order.getOrderStatus();
    }
}
