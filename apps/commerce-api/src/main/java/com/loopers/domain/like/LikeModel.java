package com.loopers.domain.like;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.user.UserModel; // 있으면 안됨, 예시로 추가된 import
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

    public static LikeModel from(Long userId, Long productId) {
        return new LikeModel(userId, productId);
    }
}
