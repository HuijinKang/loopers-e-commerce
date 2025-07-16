package com.loopers.interfaces.api.point;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "포인트", description = "포인트 관련 API")
public interface PointV1ApiSpec {

    @Operation(
            summary = "포인트 충전",
            description = "포인트를 충전합니다."
    )
    ApiResponse<Object> chargePoint(
            @Schema(name = "User ID", description = "조회할 회원의 ID")
            String userId,
            @Schema(name = "request", description = "포인트 충전 Request DTO")
            PointV1Dto.ChargeRequest request
    );

    @Operation(
            summary = "보유 포인트 조회",
            description = "사용자의 보유 포인트를 조회합니다."
    )
    ApiResponse<PointV1Dto.PointResponse> getPoint(
            @Schema(name = "User ID", description = "조회할 회원의 ID")
            String userId
    );
}
