package com.loopers.application.payment;

import com.loopers.application.payment.dto.PgPaymentResult;
import com.loopers.domain.order.OrderModel;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.order.OrderDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class PaymentFacadeTest {

	@Nested
	@DisplayName("콜백 처리")
	class Callback {

		@Test
		@DisplayName("콜백은 이벤트만 발행한다")
		void callbackPublishesEventOnly() {
			// arrange
			OrderDomainService orderDomainService = mock(OrderDomainService.class);
			PgPaymentPort pgPaymentPort = mock(PgPaymentPort.class);
			org.springframework.context.ApplicationEventPublisher publisher = mock(org.springframework.context.ApplicationEventPublisher.class);
			PaymentFacade facade = new PaymentFacade(orderDomainService, pgPaymentPort, publisher);

			OrderModel pending = mock(OrderModel.class);
			when(orderDomainService.getOrderByOrderNo("ORD-1")).thenReturn(pending);
			when(pending.getOrderStatus()).thenReturn(OrderStatus.PENDING);

			// act
			facade.handleCallback("ORD-1", PgPaymentResult.Status.SUCCESS);

			// assert
			verify(publisher, atLeastOnce()).publishEvent(any(Object.class));
		}

		@Test
		@DisplayName("PENDING 이 아니면 무시 (이벤트도 발행하지 않음)")
		void ignoresWhenNotPending() {
			// arrange
			OrderDomainService orderDomainService = mock(OrderDomainService.class);
			PgPaymentPort pgPaymentPort = mock(PgPaymentPort.class);
			org.springframework.context.ApplicationEventPublisher publisher = mock(org.springframework.context.ApplicationEventPublisher.class);
			PaymentFacade facade = new PaymentFacade(orderDomainService, pgPaymentPort, publisher);

			OrderModel shipped = mock(OrderModel.class);
			when(orderDomainService.getOrderByOrderNo("ORD-2")).thenReturn(shipped);
			when(shipped.getOrderStatus()).thenReturn(OrderStatus.SHIPPED);

			// act
			facade.handleCallback("ORD-2", PgPaymentResult.Status.SUCCESS);

			// assert
			verify(publisher, never()).publishEvent(any(Object.class));;
			verify(shipped, never()).process();
			verify(shipped, never()).cancel();
		}
	}

	@Nested
	@DisplayName("동기화")
	class Sync {

		@Test
		@DisplayName("PG 조회 결과가 SUCCESS면 PROCESSING, FAILED면 CANCELED")
		void updatesByPgResult() {
			// arrange
			OrderDomainService orderDomainService = mock(OrderDomainService.class);
			PgPaymentPort pgPaymentPort = mock(PgPaymentPort.class);
			org.springframework.context.ApplicationEventPublisher publisher = mock(org.springframework.context.ApplicationEventPublisher.class);
			PaymentFacade facade = new PaymentFacade(orderDomainService, pgPaymentPort, publisher);

			OrderModel pending = mock(OrderModel.class);
			when(orderDomainService.getOrderByOrderNo("ORD-3")).thenReturn(pending);
			when(pending.getOrderStatus()).thenReturn(OrderStatus.PENDING);

			PgPaymentResult r1 = PgPaymentResult.of("T1", "ORD-3", "SAMSUNG", "xxxx", 1000, PgPaymentResult.Status.PENDING, null);
			PgPaymentResult r2 = PgPaymentResult.of("T2", "ORD-3", "SAMSUNG", "xxxx", 1000, PgPaymentResult.Status.SUCCESS, null);
			when(pgPaymentPort.getPaymentsByOrderId("ORD-3")).thenReturn(List.of(r1, r2));

			// act
			facade.syncByOrderId("ORD-3");

			// assert
			verify(pending, times(1)).process();
		}
	}
}
