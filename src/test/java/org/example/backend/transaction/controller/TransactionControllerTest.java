package org.example.backend.transaction.controller;

import org.example.backend.category.entity.CategoryType;
import org.example.backend.common.exception.GlobalExceptionHandler;
import org.example.backend.common.exception.InvalidRequestException;
import org.example.backend.common.exception.NotFoundException;
import org.example.backend.transaction.dto.TransactionResponse;
import org.example.backend.transaction.dto.TransactionSearchRequest;
import org.example.backend.transaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class TransactionControllerTest {

    private static final Long TEMP_USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    private TransactionResponse sampleResponse(Long id) {
        return TransactionResponse.builder()
                .id(id)
                .type(CategoryType.EXPENSE)
                .amount(10000L)
                .categoryId(10L)
                .categoryName("식비")
                .transactionDate(LocalDate.of(2026, 7, 1))
                .createdAt(LocalDateTime.of(2026, 7, 1, 12, 0))
                .build();
    }

    @Test
    void 트랜잭션_등록_성공() throws Exception {
        given(transactionService.addTransaction(eq(TEMP_USER_ID), any())).willReturn(sampleResponse(1L));

        String body = """
                {
                  "amount": 10000,
                  "type": "EXPENSE",
                  "transactionDate": "2026-07-01",
                  "categoryId": 10,
                  "description": "점심"
                }
                """;

        mockMvc.perform(post("/api/transactions")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.amount").value(10000))
                .andExpect(jsonPath("$.data.categoryName").value("식비"));
    }

    @Test
    void 트랜잭션_등록_실패_필수값_누락() throws Exception {
        String body = """
                {
                  "type": "EXPENSE",
                  "transactionDate": "2026-07-01",
                  "categoryId": 10
                }
                """;

        mockMvc.perform(post("/api/transactions")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void 트랜잭션_등록_실패_카테고리_타입_불일치() throws Exception {
        given(transactionService.addTransaction(eq(TEMP_USER_ID), any()))
                .willThrow(new InvalidRequestException("CATEGORY_TYPE_MISMATCH", "선택한 카테고리는 EXPENSE 유형입니다."));

        String body = """
                {
                  "amount": 10000,
                  "type": "INCOME",
                  "transactionDate": "2026-07-01",
                  "categoryId": 10
                }
                """;

        mockMvc.perform(post("/api/transactions")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("CATEGORY_TYPE_MISMATCH"));
    }

    @Test
    void 단건_조회_성공() throws Exception {
        given(transactionService.getTransaction(TEMP_USER_ID, 1L)).willReturn(sampleResponse(1L));

        mockMvc.perform(get("/api/transactions/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void 단건_조회_실패_존재하지_않음() throws Exception {
        given(transactionService.getTransaction(TEMP_USER_ID, 999L))
                .willThrow(new NotFoundException("NOT_FOUND_TRANSACTION", "거래 내역이 없음 + 999"));

        mockMvc.perform(get("/api/transactions/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("NOT_FOUND_TRANSACTION"));
    }

    @Test
    void 목록_조회_성공() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<TransactionResponse> page = new PageImpl<>(List.of(sampleResponse(1L), sampleResponse(2L)), pageable, 2);
        given(transactionService.getTransactionPages(eq(TEMP_USER_ID), any(TransactionSearchRequest.class), any(Pageable.class)))
                .willReturn(page);

        mockMvc.perform(get("/api/transactions")
                        .param("yearMonth", "2026-07")
                        .param("type", "EXPENSE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.meta.pagination.totalItems").value(2));
    }

    @Test
    void 목록_조회_실패_yearMonth_누락() throws Exception {
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 목록_조회_실패_yearMonth_형식_오류() throws Exception {
        mockMvc.perform(get("/api/transactions").param("yearMonth", "2026-13"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 수정_성공() throws Exception {
        given(transactionService.update(eq(TEMP_USER_ID), eq(1L), any())).willReturn(sampleResponse(1L));

        String body = """
                {
                  "amount": 20000
                }
                """;

        mockMvc.perform(put("/api/transactions/{id}", 1L)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void 삭제_성공() throws Exception {
        mockMvc.perform(delete("/api/transactions/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(transactionService).delete(TEMP_USER_ID, 1L);
    }

    @Test
    void 삭제_실패_존재하지_않음() throws Exception {
        org.mockito.Mockito.doThrow(new NotFoundException("NOT_FOUND_TRANSACTION", "거래 내역이 없음 + 1"))
                .when(transactionService).delete(TEMP_USER_ID, 1L);

        mockMvc.perform(delete("/api/transactions/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND_TRANSACTION"));
    }
}
