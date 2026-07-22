package org.example.backend.transaction.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.category.entity.CategoryType;

import java.time.YearMonth;

@Getter
@Setter
public class TransactionSearchRequest {
    private YearMonth selectedMonth;

    private CategoryType selectedType;

    private String selectedCategoryName;
}
