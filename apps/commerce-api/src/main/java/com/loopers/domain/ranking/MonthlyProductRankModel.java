package com.loopers.domain.ranking;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mv_product_rank_monthly")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlyProductRankModel extends BaseEntity {

    private Long productId;

    @Column(name = "rank_order")
    private Integer rankOrder;

    private Double score;

    @Column(name = "period_year_month")
    private String yearMonth;

    private MonthlyProductRankModel(Long productId, Integer rankOrder, Double score, String yearMonth) {
        this.productId = productId;
        this.rankOrder = rankOrder;
        this.score = score;
        this.yearMonth = yearMonth;
    }

    public static MonthlyProductRankModel of(Long productId, Integer rankOrder, Double score, String yearMonth) {
        return new MonthlyProductRankModel(productId, rankOrder, score, yearMonth);
    }
}
