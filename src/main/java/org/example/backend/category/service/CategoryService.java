package org.example.backend.category.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.category.dto.CategoryRequest;
import org.example.backend.category.dto.CategoryResponse;
import org.example.backend.category.entity.Category;
import org.example.backend.category.entity.CategoryType;
import org.example.backend.category.repository.CategoryRepository;
import org.example.backend.common.exception.DuplicateResourceException;
import org.example.backend.common.exception.InvalidRequestException;
import org.example.backend.common.exception.NotFoundException;
import org.example.backend.user.entity.User;
import org.example.backend.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService  {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // 기본 카테고리 생성 (회원가입 직후 호출)
    @Transactional
    public void seedDefaultCategories(User user) {
        if (categoryRepository.existsByUserId(user.getId()))
            return;

        List<Category> defaults = List.of(
                Category.builder().user(user).name("식비").type(CategoryType.EXPENSE).emoji("🍚").build(),
                Category.builder().user(user).name("교통").type(CategoryType.EXPENSE).emoji("🚕").build(),
                Category.builder().user(user).name("주거").type(CategoryType.EXPENSE).emoji("🏠").build(),
                Category.builder().user(user).name("문화").type(CategoryType.EXPENSE).emoji("🍿").build(),
                Category.builder().user(user).name("급여").type(CategoryType.INCOME).emoji("💵").build(),
                Category.builder().user(user).name("용돈").type(CategoryType.INCOME).emoji("🤑").build()
        );
        categoryRepository.saveAll(defaults);
    }

    // 카테고리 조회
    public Page<CategoryResponse> getCategories(Long userId, CategoryType type, Pageable pageable){ // INCOME/EXPENSE
        return categoryRepository.findByUserIdAndType(userId, type, pageable)
                .map(CategoryResponse::from);
    }

    // 카테고리 추가
    @Transactional
    public CategoryResponse addCategory(Long userId, CategoryRequest request){

        User user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException("NOT_FOUND_USER","존재하지 않는 사용자입니다. - "+userId));

        if(categoryRepository.existsByNameAndUserId(request.getCategoryName(), userId))
            throw new DuplicateResourceException("DUPLICATE_CATEGORY_NAME", "이미 존재하는 카테고리 이름입니다. - " + request.getCategoryName());

        Category category = Category.builder()
                .user(user)
                .name(request.getCategoryName())
                .emoji(request.getEmoji())
                .type(request.getType())
                .build();

        return CategoryResponse.from(categoryRepository.save(category));
    }

    // 카테고리 수정
    @Transactional
    public CategoryResponse updateCategory(Long userId, Long categoryId, CategoryRequest request){

        Category currentCty = validateCategory(userId,categoryId);

        String categoryName = request.getCategoryName() != null ? request.getCategoryName() : currentCty.getName();
        String emoji = request.getEmoji() != null ? request.getEmoji() : currentCty.getEmoji();
        CategoryType type = request.getType() != null ? request.getType() : currentCty.getType();

        currentCty.update(categoryName, emoji, type);

        return CategoryResponse.from(currentCty);
    }

    // 카테고리 삭제
    @Transactional
    public void deleteCategory(Long userId, Long categoryId){

        Category category = validateCategory(userId,categoryId);

        categoryRepository.delete(category);
    }

    public Category validateCategory(Long userId, Long categoryId){
        return categoryRepository.findByIdAndUserId(categoryId,userId).orElseThrow(()->new NotFoundException("NOT_FOUND_CATEGORY","존재하지 않는 카테고리입니다"+categoryId));
    }
}
