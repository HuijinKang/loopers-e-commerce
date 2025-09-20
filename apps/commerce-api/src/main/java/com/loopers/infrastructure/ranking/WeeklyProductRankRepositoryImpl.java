package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.WeeklyProductRankModel;
import com.loopers.domain.ranking.WeeklyProductRankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WeeklyProductRankRepositoryImpl implements WeeklyProductRankRepository {

    private final WeeklyProductRankJpaRepository jpaRepository;

    @Override
    public void deleteByYearWeek(String yearWeek) {
        jpaRepository.deleteAll(jpaRepository.findByYearWeekOrderByRankOrderAsc(yearWeek));
    }

    @Override
    public WeeklyProductRankModel save(WeeklyProductRankModel model) {
        return jpaRepository.save(model);
    }

    @Override
    public List<WeeklyProductRankModel> findByYearWeekOrderByRankOrderAsc(String yearWeek) {
        return jpaRepository.findByYearWeekOrderByRankOrderAsc(yearWeek);
    }
}
