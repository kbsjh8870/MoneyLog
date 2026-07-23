package org.example.backend.statistics.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.category.entity.CategoryType;
import org.example.backend.statistics.dto.StatisticsResponse;
import org.example.backend.statistics.repository.StatisticsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;

    @Transactional(readOnly = true)
    public StatisticsResponse monthly(Long userId, String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        long income = 0;
        long expense = 0;

        for (StatisticsResponse.TypeSum row : statisticsRepository.sumByType(userId, start, end)) {
            if (row.getType() == CategoryType.INCOME) {
                income = row.getTotal();
            } else {
                expense = row.getTotal();
            }
        }

        List<StatisticsResponse.CategorySum> byCategory = statisticsRepository.sumExpenseByCategory(userId, start, end);

        return StatisticsResponse.of(income, expense, byCategory);
    }
}
