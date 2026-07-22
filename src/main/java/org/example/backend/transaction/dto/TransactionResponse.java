package org.example.backend.transaction.dto;

import lombok.Builder;
import org.example.backend.category.entity.CategoryType;
import org.example.backend.transaction.entity.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public class TransactionResponse {
    private Long id;
    private CategoryType type;
    private Long amount;
    private Long categoryId;
    private String categoryName;
    private LocalDate transactionDate;
    private LocalDateTime createdAt;

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
