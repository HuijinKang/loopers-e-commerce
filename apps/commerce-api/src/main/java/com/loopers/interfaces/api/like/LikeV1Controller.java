package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeFacade;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like/products")
public class LikeV1Controller implements LikeV1Spec {

    private final LikeFacade likeFacade;

    @Override
    @PostMapping("/{productId}")
    public ApiResponse<Void> like(@RequestHeader("X-USER-ID") String email, @PathVariable Long productId) {
        likeFacade.toggleLike(email, productId);
        return ApiResponse.success(null);
    }

    @Override
    @DeleteMapping("/{productId}")
    public ApiResponse<Void> unlike(@RequestHeader("X-USER-ID") String email, @PathVariable Long productId) {
        likeFacade.toggleLike(email, productId);
        return ApiResponse.success(null);
    }

    @Override
    @GetMapping
    public ApiResponse<Boolean> isLiked(@RequestHeader("X-USER-ID") String email, @RequestParam(required = false) Long productId) {
        if (productId == null) {
            return ApiResponse.success(null);
        }
        return ApiResponse.success(likeFacade.isLiked(email, productId));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<java.util.List<Long>> getMyLikedProducts(@RequestHeader("X-USER-ID") String email) {
        return ApiResponse.success(likeFacade.getLikedProductIds(email));
    }
}
