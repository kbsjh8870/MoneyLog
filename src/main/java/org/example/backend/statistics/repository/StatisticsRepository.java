package org.example.backend.statistics.repository;

import org.example.backend.statistics.dto.StatisticsResponse;
import org.example.backend.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<Transaction,Long> {

    // 타입별 합계
    @Query("""
            SELECT new org.example.backend.statistics.dto.StatisticsResponse$TypeSum(t.type, COALESCE(SUM(t.amount), 0))
            FROM Transaction t
            WHERE t.user.id = :userId
              AND t.transactionDate BETWEEN :start AND :end
            GROUP BY t.type
            """)
    List<StatisticsResponse.TypeSum> sumByType(@Param("userId") Long userId,
                                                @Param("start") LocalDate start,
                                                @Param("end") LocalDate end);

    // 카테고리별 지출 합계
    @Query("""
            SELECT new org.example.backend.statistics.dto.StatisticsResponse$CategorySum(c.name, COALESCE(SUM(t.amount), 0))
            FROM Transaction t JOIN t.category c
            WHERE t.user.id = :userId
              AND t.type = org.example.backend.category.entity.CategoryType.EXPENSE
              AND t.transactionDate BETWEEN :start AND :end
            GROUP BY c.id, c.name
            ORDER BY SUM(t.amount) DESC
            """)
    List<StatisticsResponse.CategorySum> sumExpenseByCategory(@Param("userId") Long userId,
                                                               @Param("start") LocalDate start,
                                                               @Param("end") LocalDate end);
}
