package org.example.backend.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.backend.category.entity.CategoryType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
public class TransactionSearchRequest {

    @DateTimeFormat(pattern = "yyyy-MM")
    private YearMonth selectedMonth;

    private CategoryType selectedType;

    private String selectedCategoryName;
}
