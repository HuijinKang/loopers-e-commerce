package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.MonthlyProductRankModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonthlyProductRankJpaRepository extends JpaRepository<MonthlyProductRankModel, Long> {
    List<MonthlyProductRankModel> findByYearMonthOrderByRankOrderAsc(String yearMonth);
}
