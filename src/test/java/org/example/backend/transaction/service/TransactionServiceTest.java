package org.example.backend.transaction.service;

import org.example.backend.category.entity.Category;
import org.example.backend.category.entity.CategoryType;
import org.example.backend.category.repository.CategoryRepository;
import org.example.backend.common.exception.InvalidRequestException;
import org.example.backend.common.exception.NotFoundException;
import org.example.backend.transaction.dto.TransactionRequest;
import org.example.backend.transaction.dto.TransactionResponse;
import org.example.backend.transaction.dto.TransactionSearchRequest;
import org.example.backend.transaction.dto.TransactionUpdateRequest;
import org.example.backend.transaction.entity.Transaction;
import org.example.backend.transaction.repository.TransactionRepository;
import org.example.backend.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private static final Long USER_ID = 1L;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private TransactionService transactionService;

    private User user;
    private Category expenseCategory;
    private Category incomeCategory;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(categoryRepository, transactionRepository);

        user = User.builder().email("test@test.com").password("pw").nickname("tester").build();
        ReflectionTestUtils.setField(user, "id", USER_ID);

        expenseCategory = Category.builder().user(user).name("식비").type(CategoryType.EXPENSE).emoji("🍚").build();
        ReflectionTestUtils.setField(expenseCategory, "id", 10L);

        incomeCategory = Category.builder().user(user).name("월급").type(CategoryType.INCOME).emoji("💰").build();
        ReflectionTestUtils.setField(incomeCategory, "id", 20L);
    }

    private TransactionRequest buildRequest(Long amount, CategoryType type, LocalDate date, Long categoryId, String description) {
        TransactionRequest request = new TransactionRequest();
        ReflectionTestUtils.setField(request, "amount", amount);
        ReflectionTestUtils.setField(request, "type", type);
        ReflectionTestUtils.setField(request, "transactionDate", date);
        ReflectionTestUtils.setField(request, "categoryId", categoryId);
        ReflectionTestUtils.setField(request, "description", description);
        return request;
    }

    private TransactionUpdateRequest buildUpdateRequest(Long amount, CategoryType type, LocalDate date, Long categoryId, String description) {
        TransactionUpdateRequest request = new TransactionUpdateRequest();
        ReflectionTestUtils.setField(request, "amount", amount);
        ReflectionTestUtils.setField(request, "type", type);
        ReflectionTestUtils.setField(request, "transactionDate", date);
        ReflectionTestUtils.setField(request, "categoryId", categoryId);
        ReflectionTestUtils.setField(request, "description", description);
        return request;
    }

    private Transaction buildTransaction(Long id, Category category, Long amount, String description, LocalDate date) {
        Transaction tx = Transaction.builder()
                .user(user)
                .category(category)
                .type(category.getType())
                .amount(amount)
                .description(description)
                .transactionDate(date)
                .build();
        ReflectionTestUtils.setField(tx, "id", id);
        return tx;
    }

    @Test
    void 트랜잭션_등록_성공() {
        TransactionRequest request = buildRequest(10000L, CategoryType.EXPENSE, LocalDate.of(2026, 7, 1), 10L, "점심");
        given(categoryRepository.findByIdAndUser_Id(10L, USER_ID)).willReturn(Optional.of(expenseCategory));
        given(transactionRepository.save(any(Transaction.class))).willAnswer(invocation -> {
            Transaction tx = invocation.getArgument(0);
            ReflectionTestUtils.setField(tx, "id", 100L);
            return tx;
        });

        TransactionResponse response = transactionService.addTransaction(USER_ID, request);

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getAmount()).isEqualTo(10000L);
        assertThat(response.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(response.getCategoryId()).isEqualTo(10L);
        assertThat(response.getCategoryName()).isEqualTo("식비");
    }

    @Test
    void 트랜잭션_등록_실패_존재하지_않는_카테고리() {
        TransactionRequest request = buildRequest(10000L, CategoryType.EXPENSE, LocalDate.of(2026, 7, 1), 999L, "점심");
        given(categoryRepository.findByIdAndUser_Id(999L, USER_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.addTransaction(USER_ID, request))
                .isInstanceOf(NotFoundException.class);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void 트랜잭션_등록_실패_카테고리_타입_불일치() {
        TransactionRequest request = buildRequest(10000L, CategoryType.INCOME, LocalDate.of(2026, 7, 1), 10L, "점심");
        given(categoryRepository.findByIdAndUser_Id(10L, USER_ID)).willReturn(Optional.of(expenseCategory));

        assertThatThrownBy(() -> transactionService.addTransaction(USER_ID, request))
                .isInstanceOf(InvalidRequestException.class);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void 단건_조회_성공() {
        Transaction tx = buildTransaction(1L, expenseCategory, 5000L, "커피", LocalDate.of(2026, 7, 2));
        given(transactionRepository.findByIdAndUser_Id(1L, USER_ID)).willReturn(Optional.of(tx));

        TransactionResponse response = transactionService.getTransaction(USER_ID, 1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getAmount()).isEqualTo(5000L);
        assertThat(response.getCategoryId()).isEqualTo(10L);
    }

    @Test
    void 단건_조회_실패_존재하지_않음() {
        given(transactionRepository.findByIdAndUser_Id(1L, USER_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getTransaction(USER_ID, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 페이징_조회는_리포지토리_검색_결과를_그대로_매핑한다() {
        YearMonth month = YearMonth.of(2026, 7);
        TransactionSearchRequest condition = new TransactionSearchRequest(month, CategoryType.EXPENSE, "식비");
        Pageable pageable = PageRequest.of(0, 10);

        Transaction tx1 = buildTransaction(1L, expenseCategory, 1000L, "a", LocalDate.of(2026, 7, 1));
        Transaction tx2 = buildTransaction(2L, expenseCategory, 2000L, "b", LocalDate.of(2026, 7, 2));
        Page<Transaction> repoPage = new PageImpl<>(List.of(tx1, tx2), pageable, 2);

        given(transactionRepository.search(USER_ID, month, CategoryType.EXPENSE, "식비", pageable))
                .willReturn(repoPage);

        Page<TransactionResponse> result = transactionService.getTransactionPages(USER_ID, condition, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting(TransactionResponse::getId).containsExactly(1L, 2L);
    }

    @Test
    void 수정_시_null인_필드는_기존_값을_유지한다() {
        Transaction tx = buildTransaction(1L, expenseCategory, 5000L, "기존설명", LocalDate.of(2026, 7, 1));
        given(transactionRepository.findByIdAndUser_Id(1L, USER_ID)).willReturn(Optional.of(tx));
        given(categoryRepository.findByIdAndUser_Id(10L, USER_ID)).willReturn(Optional.of(expenseCategory));

        // amount만 변경, 나머지는 null(미변경)
        TransactionUpdateRequest request = buildUpdateRequest(9999L, null, null, null, null);

        TransactionResponse response = transactionService.update(USER_ID, 1L, request);

        assertThat(response.getAmount()).isEqualTo(9999L);
        assertThat(response.getTransactionDate()).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(response.getCategoryId()).isEqualTo(10L);
        assertThat(response.getType()).isEqualTo(CategoryType.EXPENSE);
    }

    @Test
    void 수정_실패_존재하지_않는_트랜잭션() {
        given(transactionRepository.findByIdAndUser_Id(1L, USER_ID)).willReturn(Optional.empty());

        TransactionUpdateRequest request = buildUpdateRequest(9999L, null, null, null, null);

        assertThatThrownBy(() -> transactionService.update(USER_ID, 1L, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 삭제_성공() {
        Transaction tx = buildTransaction(1L, expenseCategory, 5000L, "커피", LocalDate.of(2026, 7, 2));
        given(transactionRepository.findByIdAndUser_Id(1L, USER_ID)).willReturn(Optional.of(tx));

        transactionService.delete(USER_ID, 1L);

        verify(transactionRepository).delete(tx);
    }

    @Test
    void 삭제_실패_존재하지_않는_트랜잭션() {
        given(transactionRepository.findByIdAndUser_Id(1L, USER_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.delete(USER_ID, 1L))
                .isInstanceOf(NotFoundException.class);

        verify(transactionRepository, never()).delete(any());
    }
}
