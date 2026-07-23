package org.example.backend.transaction.service;

import lombok.RequiredArgsConstructor;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    // 트랜잭션 추가
    @Transactional
    public TransactionResponse addTransaction(Long userId, TransactionRequest request){

        Category category = validateCategoryNType(userId, request.getCategoryId(), request.getType());

        Transaction newTransaction = Transaction.builder()
                .user(category.getUser())
                .category(category)
                .type(category.getType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .transactionDate(request.getTransactionDate())
                .build();

        return TransactionResponse.from(transactionRepository.save(newTransaction));
    }

    // 단건 조회
    public TransactionResponse getTransaction(Long userId, Long transactionId){

        Transaction tx = findOwned(transactionId,userId);

        return TransactionResponse.from(tx);
    }

    // 다건 조회 (페이징)
    public Page<TransactionResponse> getTransactionPages(Long userId, TransactionSearchRequest condition, Pageable pageable){
        return transactionRepository.search(
                        userId,
                        condition.getSelectedMonth(),
                        condition.getSelectedType(),
                        condition.getSelectedCategoryName(),
                        pageable)
                .map(TransactionResponse::from);
    }

    // 트랜잭션 수정
    @Transactional
    public TransactionResponse update(Long userId, Long transactionId, TransactionUpdateRequest request){

        Transaction currentTx = findOwned(transactionId, userId);

        Long categoryId = request.getCategoryId() != null ? request.getCategoryId() : currentTx.getCategory().getId();
        Category category = getOwnedCategory(userId, categoryId);

        Long amount = request.getAmount() != null ? request.getAmount() : currentTx.getAmount();
        String description = request.getDescription() != null ? request.getDescription() : currentTx.getDescription();
        LocalDate transactionDate = request.getTransactionDate() != null ? request.getTransactionDate() : currentTx.getTransactionDate();

        currentTx.update(category, category.getType(), amount, description, transactionDate);

        return TransactionResponse.from(currentTx);
    }

    // 트랜잭션 삭제
    @Transactional
    public void delete(Long userId, Long transactionId){
        Transaction transaction = findOwned(transactionId,userId);

        transactionRepository.delete(transaction);
    }

    public Transaction findOwned(Long transactionId, Long userId){
        return transactionRepository.findByIdAndUserId(transactionId,userId).orElseThrow(()-> new NotFoundException("NOT_FOUND_TRANSACTION","거래 내역이 없음 + "+transactionId));
    }

    public Category validateCategoryNType(Long userId, Long categoryId, CategoryType type){
        Category category = getOwnedCategory(userId, categoryId);

        if (category.getType() != type) {
            throw new InvalidRequestException("CATEGORY_TYPE_MISMATCH",
                    "선택한 카테고리(" + category.getName() + ")는 " + category.getType() + " 유형입니다.");
        }

        return category;
    }

    public Category getOwnedCategory(Long userId, Long categoryId){
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new NotFoundException("NOT_FOUND_CATEGORY","존재하지 않는 카테고리 - " + categoryId));
    }
}
