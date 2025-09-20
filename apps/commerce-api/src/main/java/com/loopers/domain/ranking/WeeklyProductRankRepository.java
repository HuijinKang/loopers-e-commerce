package com.loopers.domain.ranking;

import java.util.List;

public interface WeeklyProductRankRepository {
    void deleteByYearWeek(String yearWeek);
    WeeklyProductRankModel save(WeeklyProductRankModel model);
    List<WeeklyProductRankModel> findByYearWeekOrderByRankOrderAsc(String yearWeek);
}
