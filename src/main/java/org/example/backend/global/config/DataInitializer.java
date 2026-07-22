package org.example.backend.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.category.entity.Category;
import org.example.backend.category.entity.CategoryType;
import org.example.backend.category.repository.CategoryRepository;
import org.example.backend.transaction.entity.Transaction;
import org.example.backend.transaction.repository.TransactionRepository;
import org.example.backend.user.entity.User;
import org.example.backend.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Profile("local")
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1) 저장
        User user = userRepository.save(User.builder()
                .email("test@likelion.com")
                .password("임시")   // 평문 저장 x
                .nickname("머니로그유저")
                .build());

        Category food = categoryRepository.save(Category.builder()
                .user(user).name("식비").type(CategoryType.EXPENSE).build());
        Category salary = categoryRepository.save(Category.builder()
                .user(user).name("월급").type(CategoryType.INCOME).build());
        Category subscribe = categoryRepository.save(Category.builder()
                .user(user).name("구독료").type(CategoryType.EXPENSE).emoji("😏").build());

        transactionRepository.save(Transaction.builder()
                .user(user).category(food).type(CategoryType.EXPENSE)
                .amount(12000L).description("점심 김치찌개")
                .transactionDate(LocalDate.of(2026, 7, 8))
                .build());
        transactionRepository.save(Transaction.builder()
                .user(user).category(food).type(CategoryType.EXPENSE)
                .amount(8000L).description("저녁 라면")
                .transactionDate(LocalDate.of(2026, 7, 20))
                .build());
        transactionRepository.save(Transaction.builder()
                .user(user).category(salary).type(CategoryType.INCOME)
                .amount(3000000L).description("7월 월급")
                .transactionDate(LocalDate.of(2026, 7, 25))
                .build());
        transactionRepository.save(Transaction.builder()
                .user(user).category(food).type(CategoryType.EXPENSE)
                .amount(15000L).description("6월 외식")
                .transactionDate(LocalDate.of(2026, 6, 15))
                .build());
        transactionRepository.save(Transaction.builder()
                .user(user).category(subscribe).type(CategoryType.EXPENSE)
                .amount(15000L).description("6월 구독료")
                .transactionDate(LocalDate.of(2026, 6, 15))
                .build());

        // 2) 조회 확인
        long count = transactionRepository.count();
        log.info("저장된 거래 수 = {}", count);
        transactionRepository.findAll()
                .forEach(t -> log.info("거래: {}원 / {} / {}",
                        t.getAmount(), t.getCategory().getName(), t.getTransactionDate()));

        List<Category> byUser = categoryRepository.findByUser(user);
        for(Category c : byUser){
            log.info(c.getEmoji());
        }


        // 3) search(...) 필터 조합 확인
        PageRequest pageRequest = PageRequest.of(0, 10);
        logSearch("필터 없음", user.getId(), null, null, null, pageRequest);
        logSearch("2026-07월만", user.getId(), YearMonth.of(2026, 7), null, null, pageRequest);
        logSearch("지출만", user.getId(), null, CategoryType.EXPENSE, null, pageRequest);
        logSearch("식비 카테고리만", user.getId(), null, null, "식비", pageRequest);
        logSearch("2026-07월 + 지출", user.getId(), YearMonth.of(2026, 7), CategoryType.EXPENSE, null, pageRequest);
        logSearch("2026-07월 + 지출 + 식비", user.getId(), YearMonth.of(2026, 7), CategoryType.EXPENSE, "식비", pageRequest);
    }

    private void logSearch(String label, Long userId, YearMonth month, CategoryType type,
                            String categoryName, PageRequest pageRequest) {
        Page<Transaction> page = transactionRepository.search(userId, month, type, categoryName, pageRequest);
        log.info("[검색: {}] 총 {}건", label, page.getTotalElements());
        page.getContent().forEach(t -> log.info("  -> {}원 / {} / {}",
                t.getAmount(), t.getCategory().getName(), t.getTransactionDate()));
    }
}
