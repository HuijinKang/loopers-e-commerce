package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointInfo;
import com.loopers.domain.point.PointModel;

public class PointV1Dto {

    public record ChargeRequest(
            Long amount
    ) {
    }

    public record ChargeResponse(
            Long amount
    ) {
        public static ChargeResponse from(PointModel model) {
            return new ChargeResponse(
                    model.getAmount()
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
