package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointFacade;
import com.loopers.application.point.PointInfo;
import com.loopers.application.point.PointQueryFacade;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/points")
public class PointV1Controller implements PointV1ApiSpec {

    private final PointFacade pointFacade;
    private final PointQueryFacade pointQueryFacade;

    @Override
    @PostMapping("/charge")
    public ApiResponse<PointV1Dto.ChargeResponse> chargePoint(
            @RequestHeader("X-USER-ID") String email,
            @RequestBody PointV1Dto.ChargeRequest request
    ) {
        PointInfo pointInfo = pointFacade.chargePoint(email, request);
        PointV1Dto.ChargeResponse response = PointV1Dto.ChargeResponse.from(pointInfo);
        return ApiResponse.success(response);
    }

    @Override
    @GetMapping
    public ApiResponse<PointV1Dto.PointResponse> getPoint(
            @RequestHeader("X-USER-ID") String email
    ) {
        PointInfo pointInfo = pointQueryFacade.getPointInfo(email);
        PointV1Dto.PointResponse response = PointV1Dto.PointResponse.from(pointInfo);
        return ApiResponse.success(response);
    }
}
