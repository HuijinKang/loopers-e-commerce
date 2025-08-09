package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeFacade;
import com.loopers.application.like.LikeQueryFacade;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like/products")
public class LikeV1Controller implements LikeV1Spec {

    private final LikeFacade likeFacade;
    private final LikeQueryFacade likeQueryFacade;

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
    public ApiResponse<Boolean> isLiked(@RequestHeader("X-USER-ID") String email, @RequestParam Long productId) {
        return ApiResponse.success(likeQueryFacade.isLiked(email, productId));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<Long>> getMyLikedProducts(@RequestHeader("X-USER-ID") String email) {
        return ApiResponse.success(likeQueryFacade.getLikedProductIds(email));
    }
}
