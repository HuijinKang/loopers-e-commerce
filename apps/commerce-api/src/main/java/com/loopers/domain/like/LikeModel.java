package com.loopers.domain.like;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeModel extends BaseEntity {

    private Long userId;
    private Long productId;

    public LikeModel(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public static LikeModel of(Long userId, Long productId) {
        return new LikeModel(userId, productId);
    }
}
