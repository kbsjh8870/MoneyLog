package org.example.backend.transaction.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TransactionUpdateRequest {
    @Positive(message = "금액은 0원 이상이어야 합니다.")
    private Long amount;

    private LocalDate transactionDate;

    private Long categoryId;

    @Size(max = 255, message = "255자 이하여야 합니다.")
    private String description;
}
