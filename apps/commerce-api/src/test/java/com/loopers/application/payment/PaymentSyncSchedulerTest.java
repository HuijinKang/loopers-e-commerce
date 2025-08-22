package com.loopers.application.payment;

import com.loopers.domain.order.OrderModel;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.order.OrderDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

class PaymentSyncSchedulerTest {

    @Nested
    @DisplayName("주기 동기화")
    class SyncJob {
        @Test
        @DisplayName("PENDING 주문만 syncByOrderId 호출")
        void callsSyncOnlyForPending() {
            // arrange
            OrderDomainService orderDomainService = mock(OrderDomainService.class);
            PaymentFacade paymentFacade = mock(PaymentFacade.class);
            PaymentSyncScheduler scheduler = new PaymentSyncScheduler(orderDomainService, paymentFacade);

            OrderModel pending = mock(OrderModel.class);
            when(pending.getOrderStatus()).thenReturn(OrderStatus.PENDING);
            when(pending.getOrderNo()).thenReturn("ORD-A");

            OrderModel shipped = mock(OrderModel.class);
            when(shipped.getOrderStatus()).thenReturn(OrderStatus.SHIPPED);
            when(orderDomainService.findPendingOrdersUpdatedBefore(any(ZonedDateTime.class)))
                    .thenReturn(List.of(pending, shipped));

            // act
            scheduler.syncPendingOrders();

            // assert
            verify(paymentFacade, times(1)).syncByOrderId("ORD-A");
            verify(paymentFacade, never()).syncByOrderId("ORD-B");
        }
    }
}
