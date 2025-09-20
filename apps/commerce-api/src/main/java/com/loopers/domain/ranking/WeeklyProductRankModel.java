package com.loopers.domain.ranking;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mv_product_rank_weekly")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyProductRankModel extends BaseEntity {

    private Long productId;

    @Column(name = "rank_order")
    private Integer rankOrder;

    private Double score;


    @Column(name = "period_year_week")
    private String yearWeek;

    private WeeklyProductRankModel(Long productId, Integer rankOrder, Double score, String yearWeek) {
        this.productId = productId;
        this.rankOrder = rankOrder;
        this.score = score;
        this.yearWeek = yearWeek;
    }

    public static WeeklyProductRankModel of(Long productId, Integer rankOrder, Double score, String yearWeek) {
        return new WeeklyProductRankModel(productId, rankOrder, score, yearWeek);
    }
}
