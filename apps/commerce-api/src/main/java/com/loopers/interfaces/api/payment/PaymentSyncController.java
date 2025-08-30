package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentFacade;
import com.loopers.domain.order.OrderStatus;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentSyncController {

    private final PaymentFacade paymentFacade;

    @PostMapping("/sync")
    public ApiResponse<PaymentSyncDto.SyncResponse> sync(@RequestParam("orderId") String orderId) {
        OrderStatus status = paymentFacade.syncByOrderId(orderId);
        return ApiResponse.success(new PaymentSyncDto.SyncResponse(orderId, status.name()));
    }
}
