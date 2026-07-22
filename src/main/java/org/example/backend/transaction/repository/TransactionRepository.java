package org.example.backend.transaction.repository;

import org.example.backend.transaction.entity.Transaction;
import org.example.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, TransactionRepositoryCustom {
    // 기간 내 전체 조회
    List<Transaction> findByUserAndTransactionDateBetween(User user, LocalDate transactionDateAfter, LocalDate transactionDateBefore);

    Optional<Transaction> findByIdAndUser(Long id, User user);
}
