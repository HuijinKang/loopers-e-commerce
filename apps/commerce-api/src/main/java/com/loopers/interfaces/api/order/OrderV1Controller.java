package com.loopers.interfaces.api.order;


import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.OrderInfo;
import com.loopers.application.order.OrderQueryFacade;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderV1Controller implements OrderV1ApiSpec {

    private final OrderFacade  orderFacade;
    private final OrderQueryFacade orderQueryFacade;


    @Override
    @PostMapping
    public ApiResponse<Long> placeOrder(
            @RequestHeader("X-USER-ID") String email,
            @RequestBody OrderV1Dto.CreateOrderRequest request
    ) {
        Long orderId = orderFacade.placeOrder(email, request);
        return ApiResponse.success(orderId);
    }

    @GetMapping
    public ApiResponse<java.util.List<OrderV1Dto.OrderResponse>> getOrders(
            @RequestHeader("X-USER-ID") String email
    ) {
        java.util.List<OrderV1Dto.OrderResponse> responses = orderQueryFacade.getOrders(email)
                .stream().map(OrderV1Dto.OrderResponse::from).toList();
        return ApiResponse.success(responses);
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderV1Dto.OrderResponse> getOrder(
            @RequestHeader("X-USER-ID") String email,
            @PathVariable Long orderId
    ) {
        OrderInfo info = orderQueryFacade.getOrder(email, orderId);
        return ApiResponse.success(OrderV1Dto.OrderResponse.from(info));
    }
}
