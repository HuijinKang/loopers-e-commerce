package com.loopers.application.point;

import com.loopers.domain.point.PointModel;

public record PointInfo(
        Long amount
) {
    public static PointInfo from(PointModel model) {
        return new PointInfo(
                model.getAmount()
        );
    }
}
