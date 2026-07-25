package org.example.backend.transaction.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.backend.category.entity.CategoryType;
import org.example.backend.transaction.entity.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionResponse {
    private final Long id;
    private final CategoryType type;
    private final Long amount;
    private final Long categoryId;
    private final String categoryName;
    private final LocalDate transactionDate;
    private final LocalDateTime createdAt;

    public static TransactionResponse from(Transaction transaction){
        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .transactionDate(transaction.getTransactionDate())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
