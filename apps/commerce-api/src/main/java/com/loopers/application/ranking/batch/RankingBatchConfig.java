package com.loopers.application.ranking.batch;

import com.loopers.domain.ranking.MonthlyProductRankModel;
import com.loopers.domain.ranking.MonthlyProductRankRepository;
import com.loopers.domain.ranking.RankingUnionPort;
import com.loopers.domain.ranking.WeeklyProductRankModel;
import com.loopers.domain.ranking.WeeklyProductRankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RankingBatchConfig {

    private final RankingUnionPort rankingUnionPort;
    private final WeeklyProductRankRepository weeklyRepo;
    private final MonthlyProductRankRepository monthlyRepo;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Bean
    public Job weeklyMonthlyRankingJob(JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new JobBuilder("weeklyMonthlyRankingJob", jobRepository)
                .start(weeklyStep(jobRepository, txManager))
                .next(monthlyStep(jobRepository, txManager))
                .build();
    }

    @Bean
    public Step weeklyStep(JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new StepBuilder("weeklyRankingStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String dateParam = (String) chunkContext.getStepContext().getJobParameters().get("targetDate");
                    LocalDate target = dateParam == null ? LocalDate.now().minusDays(1) : LocalDate.parse(dateParam, DF);
                    LocalDate weekStart = target.minusDays(target.getDayOfWeek().getValue() % 7);
                    LocalDate weekEnd = weekStart.plusDays(6);
                    Map<Long, Double> scores = rankingUnionPort.aggregateScores(weekStart, weekEnd);
                    List<Map.Entry<Long, Double>> top = scores.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .limit(100)
                            .toList();
                    String yearWeek = target.getYear() + "W" + String.format("%02d", target.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear()));
                    weeklyRepo.deleteByYearWeek(yearWeek);
                    int rank = 1;
                    for (Map.Entry<Long, Double> e : top) {
                        weeklyRepo.save(WeeklyProductRankModel.of(e.getKey(), rank++, e.getValue(), yearWeek));
                    }
                    return org.springframework.batch.repeat.RepeatStatus.FINISHED;
                }, txManager)
                .build();
    }

    @Bean
    public Step monthlyStep(JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new StepBuilder("monthlyRankingStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String dateParam = (String) chunkContext.getStepContext().getJobParameters().get("targetDate");
                    LocalDate target = dateParam == null ? LocalDate.now().minusDays(1) : LocalDate.parse(dateParam, DF);
                    LocalDate first = target.withDayOfMonth(1);
                    LocalDate last = target.withDayOfMonth(target.lengthOfMonth());
                    Map<Long, Double> scores = rankingUnionPort.aggregateScores(first, last);
                    List<Map.Entry<Long, Double>> top = scores.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .limit(100)
                            .toList();
                    String yearMonth = target.format(DateTimeFormatter.ofPattern("yyyyMM"));
                    monthlyRepo.deleteByYearMonth(yearMonth);
                    int rank = 1;
                    for (Map.Entry<Long, Double> e : top) {
                        monthlyRepo.save(MonthlyProductRankModel.of(e.getKey(), rank++, e.getValue(), yearMonth));
                    }
                    return org.springframework.batch.repeat.RepeatStatus.FINISHED;
                }, txManager)
                .build();
    }
}
