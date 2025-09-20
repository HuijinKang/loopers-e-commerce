package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingUnionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RankingRedisUnionAdapter implements RankingUnionPort {

    private final RedisTemplate<String, String> redisTemplate;
    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public Map<Long, Double> aggregateScores(LocalDate fromInclusive, LocalDate toInclusive) {
        Map<Long, Double> acc = new HashMap<>();
        LocalDate d = fromInclusive;
        while (!d.isAfter(toInclusive)) {
            String key = RankingKey.daily(d);
            Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
            if (tuples != null) {
                for (ZSetOperations.TypedTuple<String> t : tuples) {
                    if (t.getValue() == null || t.getScore() == null) continue;
                    Long pid = Long.valueOf(t.getValue());
                    acc.merge(pid, t.getScore(), Double::sum);
                }
            }
            d = d.plusDays(1);
        }
        return acc;
    }
}
