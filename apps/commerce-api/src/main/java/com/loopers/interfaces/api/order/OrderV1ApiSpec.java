package com.loopers.interfaces.api.order;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "주문", description = "주문 관련 API")
public interface OrderV1ApiSpec {

    // 주문
    @Operation(
            summary = "보유 포인트 조회",
            description = "사용자의 보유 포인트를 조회합니다."
    )
    ApiResponse<Long> placeOrder(
    );

}
