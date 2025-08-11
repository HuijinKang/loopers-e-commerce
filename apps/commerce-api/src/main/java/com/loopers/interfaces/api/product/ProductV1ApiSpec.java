package com.loopers.interfaces.api.product;

import com.loopers.domain.product.ProductSortType;
import com.loopers.domain.product.ProductStatus;
import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "상품", description = "상품 조회 API")
public interface ProductV1ApiSpec {

    @Operation(
            summary = "상품 목록 조회",
            description = "페이지, 정렬, 상태, 브랜드 필터를 적용해 상품 목록을 조회합니다."
    )
    ApiResponse<List<ProductV1Dto.ProductSummaryResponse>> getProducts(
            @Schema(description = "페이지 번호", example = "0")
            int page,
            @Schema(description = "페이지 크기", example = "10")
            int size,
            @Schema(description = "정렬 기준", example = "LIKES_DESC")
            ProductSortType sortType,
            @Schema(description = "상품 상태 필터", example = "ON_SALE")
            ProductStatus status,
            @Schema(description = "브랜드 ID 필터", example = "1")
            Long brandId
    );

    @Operation(
            summary = "상품 상세 조회",
            description = "상품 ID로 단건 상품 상세를 조회합니다."
    )
    ApiResponse<ProductV1Dto.ProductSummaryResponse> getProduct(
            @Schema(description = "상품 ID", example = "1")
            Long productId
    );
}


