package org.example.backend.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.backend.category.entity.CategoryType;

import java.util.List;

@AllArgsConstructor
@Getter
public class StatisticsResponse {

    private final long income;
    private final long expense;
    private final long balance;
    private final List<CategorySum> byCategory;

    public static StatisticsResponse of(long income, long expense, List<CategorySum> byCategory) {
        return new StatisticsResponse(income, expense, income - expense, byCategory);
    }

    @Getter
    public static class CategorySum {

        private final String categoryName;
        private final long total;

        public CategorySum(String categoryName, long total) {
            this.categoryName = categoryName;
            this.total = total;
        }

    }

    @Getter
    public static class TypeSum {

        private final CategoryType type;
        private final long total;

        public TypeSum(CategoryType type, long total) {
            this.type = type;
            this.total = total;
        }

    }
}
