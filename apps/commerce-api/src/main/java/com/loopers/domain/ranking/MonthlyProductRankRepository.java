package com.loopers.domain.ranking;

import java.util.List;

public interface MonthlyProductRankRepository {
    void deleteByYearMonth(String yearMonth);
    MonthlyProductRankModel save(MonthlyProductRankModel model);
    List<MonthlyProductRankModel> findByYearMonthOrderByRankOrderAsc(String yearMonth);
}
