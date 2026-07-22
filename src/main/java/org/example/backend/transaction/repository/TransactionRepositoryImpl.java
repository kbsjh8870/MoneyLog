package org.example.backend.transaction.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.backend.category.entity.CategoryType;
import org.example.backend.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.example.backend.transaction.entity.QTransaction.transaction;

@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Transaction> search(Long userId, YearMonth month, CategoryType type, String categoryName, Pageable pageable) {
        List<Transaction> content = queryFactory
                .selectFrom(transaction)
                .where(
                        transaction.user.id.eq(userId),
                        monthEq(month),
                        typeEq(type),
                        categoryNameEq(categoryName)
                )
                .orderBy(transaction.transactionDate.desc(), transaction.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(transaction.count())
                .from(transaction)
                .where(
                        transaction.user.id.eq(userId),
                        monthEq(month),
                        typeEq(type),
                        categoryNameEq(categoryName)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression monthEq(YearMonth month) {
        if (month == null) {
            return null;
        }
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        return transaction.transactionDate.between(start, end);
    }

    private BooleanExpression typeEq(CategoryType type) {
        return type != null ? transaction.type.eq(type) : null;
    }

    private BooleanExpression categoryNameEq(String categoryName) {
        return (categoryName != null && !categoryName.isBlank())
                ? transaction.category.name.eq(categoryName)
                : null;
    }
}