package org.example.backend.transaction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.example.backend.category.entity.CategoryType;

import java.time.LocalDate;

@Getter
public class TransactionRequest {
    @NotNull(message = "금액을 입력해주세요.")
    @Positive(message = "금액은 0원 이상이어야 합니다.")
    private Long amount;

    @NotNull(message = "지출/수입 유형을 선택해주세요.")
    private CategoryType type;

    @NotNull(message = "거래 날짜를 입력해주세요. ex) 011012")
    @Pattern(regexp = "^\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])$", message = "날짜 형식을 YYMMDD(6자리 숫자)로 맞춰주세요.")
    private LocalDate transactionDate;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Long categoryId;

    @Size(message = "255자 이하여야 합니다.")
    private String description;
}
