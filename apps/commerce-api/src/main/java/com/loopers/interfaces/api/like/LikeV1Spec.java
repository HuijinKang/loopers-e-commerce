package com.loopers.interfaces.api.like;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "좋아요", description = "좋아요 관련 API")
public interface LikeV1Spec {

    @Operation(summary = "상품 좋아요 등록", description = "상품에 좋아요를 등록합니다.")
    ApiResponse<Void> like(String email, Long productId);

    @Operation(summary = "상품 좋아요 취소", description = "상품에 등록된 좋아요를 취소합니다.")
    ApiResponse<Void> unlike(String email, Long productId);

    @Operation(summary = "좋아요 여부 조회", description = "현재 사용자가 해당 상품을 좋아요 했는지 여부를 조회합니다.")
    ApiResponse<Boolean> isLiked(String email, Long productId);

    @Operation(summary = "좋아요 목록 조회", description = "현재 사용자가 좋아요한 상품 ID 목록을 조회합니다.")
    ApiResponse<List<Long>> getMyLikedProducts(String email);
}
