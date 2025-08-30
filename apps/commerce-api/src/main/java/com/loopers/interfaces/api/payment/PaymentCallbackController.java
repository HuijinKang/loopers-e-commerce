package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentCallbackController {

    private final PaymentFacade paymentFacade;

    @PostMapping("/callback")
    public void callback(@RequestBody PaymentCallbackDto.CallbackRequest payload) {
        paymentFacade.handleCallback(payload.orderId(), payload.status());
    }
}
