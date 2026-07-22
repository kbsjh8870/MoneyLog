package org.example.backend.transaction.repository;

import org.example.backend.category.entity.CategoryType;
import org.example.backend.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.YearMonth;

public interface TransactionRepositoryCustom {

    Page<Transaction> search(Long userId, YearMonth month, CategoryType type, String categoryName, Pageable pageable);
}