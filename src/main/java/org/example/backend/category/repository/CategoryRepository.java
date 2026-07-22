package org.example.backend.category.repository;

import org.example.backend.category.entity.Category;
import org.example.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    Optional<Category> findByUser(User user); // 사용자별 카테고리
}
