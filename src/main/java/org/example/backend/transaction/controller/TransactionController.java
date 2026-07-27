package org.example.backend.transaction.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.category.entity.Category;
import org.example.backend.category.entity.CategoryType;
import org.example.backend.common.response.ApiResponse;
import org.example.backend.common.response.PageMeta;
import org.example.backend.security.CustomUserDetails;
import org.example.backend.transaction.dto.TransactionRequest;
import org.example.backend.transaction.dto.TransactionResponse;
import org.example.backend.transaction.dto.TransactionSearchRequest;
import org.example.backend.transaction.dto.TransactionUpdateRequest;
import org.example.backend.transaction.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;


    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> addTx(@Valid @RequestBody TransactionRequest request,
                                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails){
        TransactionResponse txResponse = transactionService.addTransaction(customUserDetails.getUserId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("트랜잭션 등록 완료",txResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTx(@PathVariable Long id,
                                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails){
        TransactionResponse txResponse = transactionService.getTransaction(customUserDetails.getUserId(), id);

        return ResponseEntity.ok(ApiResponse.success(id + "번 트랜잭션 조회 완료",txResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTxList(@RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
                                                                            @RequestParam(required = false) CategoryType type,
                                                                            @RequestParam(required = false) String categoryName,
                                                                            @PageableDefault(size= 5) Pageable pageable,
                                                                            @AuthenticationPrincipal CustomUserDetails customUserDetails){

        TransactionSearchRequest tsr = new TransactionSearchRequest(yearMonth, type, categoryName);

        Page<TransactionResponse> pages = transactionService.getTransactionPages(customUserDetails.getUserId(), tsr, pageable);

        return ResponseEntity.ok(ApiResponse.success("트랜잭션 페이징 목록 조회 완료",pages.getContent(), PageMeta.from(pages)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> updateTx(@PathVariable Long id,
                                                                     @Valid @RequestBody TransactionUpdateRequest request,
                                                                     @AuthenticationPrincipal CustomUserDetails customUserDetails){
        TransactionResponse txResponse = transactionService.update(customUserDetails.getUserId(), id, request);

        return ResponseEntity.ok(ApiResponse.success(id + "번 트랜잭션 수정 완료",txResponse));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @AuthenticationPrincipal CustomUserDetails customUserDetails){
        transactionService.delete(customUserDetails.getUserId(), id);

        return ApiResponse.success(id+"번 트랜잭션 삭제 완료",null);
    }
}
