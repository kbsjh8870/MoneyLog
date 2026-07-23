package org.example.backend.category.repository;

import org.example.backend.category.entity.Category;
import org.example.backend.category.entity.CategoryType;
import org.example.backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    List<Category> findByUser(User user); // 사용자별 카테고리

    Optional<Category> findByIdAndUserId(Long id, Long userId);

    Boolean existsByUserId(Long userId);

    Boolean existsByNameAndUserId(String name, Long userId);

    Page<Category> findByUserIdAndType(Long userId, CategoryType type, Pageable pageable);
}
