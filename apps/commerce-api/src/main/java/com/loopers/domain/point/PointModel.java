package com.loopers.domain.point;

import com.loopers.domain.user.UserModel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "points")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModel user;

    private Long amount;

    public PointModel(UserModel user, Long amount) {
        this.user = user;
        this.amount = amount;
    }

    public static PointModel of(UserModel user, Long amount) {
        return new PointModel(user, amount);
    }

    public void charge(Long amount) {
        this.amount += amount;
    }
}
