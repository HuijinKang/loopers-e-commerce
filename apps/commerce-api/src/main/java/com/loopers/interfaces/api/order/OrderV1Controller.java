package com.loopers.interfaces.api.order;


import com.loopers.application.order.OrderFacade;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderV1Controller implements OrderV1ApiSpec {

    private final OrderFacade  orderFacade;


    @Override
    public ApiResponse<Long> placeOrder(
            @RequestBody OrderRequest request
    ) {
        Long orderId = orderFacade.placeOrder(request.toCommand());
        return ApiResponse.success(orderId);
    }
}
