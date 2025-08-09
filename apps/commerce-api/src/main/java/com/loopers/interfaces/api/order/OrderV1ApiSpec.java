package com.loopers.interfaces.api.order;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "주문", description = "주문 관련 API")
public interface OrderV1ApiSpec {

    @Operation(
            summary = "주문 생성",
            description = "주문을 생성합니다."
    )
    ApiResponse<Long> placeOrder(
            @Schema(name = "X-USER-ID", description = "요청 사용자 식별자(헤더)")
            String userId,
            @Schema(name = "request", description = "주문 생성 Request DTO")
            OrderV1Dto.CreateOrderRequest request
    );

}
