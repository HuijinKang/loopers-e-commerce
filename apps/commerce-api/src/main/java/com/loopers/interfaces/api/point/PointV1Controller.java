package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointFacade;
import com.loopers.application.point.PointInfo;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/points")
public class PointV1Controller implements PointV1ApiSpec {

    private final PointFacade pointFacade;

    @Override
    @PostMapping("/charge")
    public ApiResponse<Object> chargePoint(
            @RequestHeader("X-USER-ID") String userId,
            @RequestBody PointV1Dto.ChargeRequest request
    ) {
        Long amount = pointFacade.chargePoint(userId, request);
        return ApiResponse.success(amount);
    }

    @Override
    @GetMapping
    public ApiResponse<PointV1Dto.PointResponse> getPoint(
            @RequestHeader("X-USER-ID") String userId
    ) {
        PointInfo pointInfo = pointFacade.getPointInfo(userId);

        if (pointInfo == null) {
            return null;
        }

        PointV1Dto.PointResponse response = PointV1Dto.PointResponse.from(pointInfo);
        return ApiResponse.success(response);
    }
}
