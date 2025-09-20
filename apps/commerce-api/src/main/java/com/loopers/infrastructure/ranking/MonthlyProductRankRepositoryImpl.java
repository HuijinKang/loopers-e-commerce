package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.MonthlyProductRankModel;
import com.loopers.domain.ranking.MonthlyProductRankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MonthlyProductRankRepositoryImpl implements MonthlyProductRankRepository {

    private final MonthlyProductRankJpaRepository jpaRepository;

    @Override
    public void deleteByYearMonth(String yearMonth) {
        jpaRepository.deleteAll(jpaRepository.findByYearMonthOrderByRankOrderAsc(yearMonth));
    }

    @Override
    public MonthlyProductRankModel save(MonthlyProductRankModel model) {
        return jpaRepository.save(model);
    }

    @Override
    public List<MonthlyProductRankModel> findByYearMonthOrderByRankOrderAsc(String yearMonth) {
        return jpaRepository.findByYearMonthOrderByRankOrderAsc(yearMonth);
    }
}
