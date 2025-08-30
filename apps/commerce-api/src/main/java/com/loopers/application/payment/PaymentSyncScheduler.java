package com.loopers.application.payment;

import com.loopers.domain.order.OrderDomainService;
import com.loopers.domain.order.OrderModel;
import com.loopers.domain.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class PaymentSyncScheduler {

    private final OrderDomainService orderDomainService;
    private final PaymentFacade paymentFacade;

    @Scheduled(fixedDelay = 60000)
    public void syncPendingOrders() {
        ZonedDateTime threshold = ZonedDateTime.now().minusMinutes(1);
        List<OrderModel> pending = orderDomainService.findPendingOrdersUpdatedBefore(threshold);
        for (OrderModel order : pending) {
            if (order.getOrderStatus() == OrderStatus.PENDING) {
                try {
                    OrderStatus status = paymentFacade.syncByOrderId(order.getOrderNo());
                    if (status == OrderStatus.PENDING) {
                        // 일정 시간 경과에도 거래 내역이 없거나 미확정이면 주문을 취소하여 영구 대기를 방지.
                        orderDomainService.cancelIfPending(order.getOrderNo());
                    }
                } catch (Exception ignored) {}
            }
        }
    }
}
