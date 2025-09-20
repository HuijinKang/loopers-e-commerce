package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.WeeklyProductRankModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyProductRankJpaRepository extends JpaRepository<WeeklyProductRankModel, Long> {
    List<WeeklyProductRankModel> findByYearWeekOrderByRankOrderAsc(String yearWeek);
}
