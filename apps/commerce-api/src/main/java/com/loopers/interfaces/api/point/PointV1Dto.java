package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointInfo;

public class PointV1Dto {

    public record ChargeRequest(
            Long amount
    ) {
    }

    public record ChargeResponse(
            Long amount
    ) {
        public static ChargeResponse from(PointInfo pointInfo) {
            return new ChargeResponse(
                    pointInfo.amount()
            );
        }
    }

    public record PointResponse(
            Long amount
    ) {
        public static PointResponse from(PointInfo pointInfo) {
            return new PointResponse(
                    pointInfo.amount()
            );
        }
    }
}
