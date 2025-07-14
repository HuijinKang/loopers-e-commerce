package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User V1 API", description = "User API 입니다.")
public interface UserV1ApiSpec {

    @Operation(
            summary = "회원 가입",
            description = "회원 가입을 진행합니다."
    )
    ApiResponse<Object> createUser(
            @Schema(name = "request", description = "회원 가입 Request DTO")
            UserV1Dto.UserRequest request
    );

    @Operation(
            summary = "회원 조회",
            description = "User ID로 회원을 조회합니다."
    )
    ApiResponse<UserV1Dto.UserResponse> getUser(
            @Schema(name = "User ID", description = "조회할 회원의 ID")
            String userId
    );
}
