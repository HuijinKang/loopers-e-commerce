package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "points")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointModel extends BaseEntity {

    private Long userId;
    private Long amount;
    @Version
    private Long version;

    public PointModel(Long userId, Long amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public static PointModel of(Long userId, Long amount) {
        return new PointModel(userId, amount);
    }

    public void charge(Long amount) {
        if (amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "충전 금액은 0보다 커야 합니다.");
        }

        this.amount += amount;
    }

    public void deduct(Long amount) {
        if (amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용 금액은 0보다 커야 합니다.");
        }

        if (this.amount < amount) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트가 부족합니다.");
        }

        this.amount -= amount;
    }
}
