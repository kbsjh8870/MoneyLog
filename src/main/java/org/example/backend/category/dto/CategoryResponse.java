package org.example.backend.category.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.backend.category.entity.Category;
import org.example.backend.category.entity.CategoryType;

@Getter
@Builder
public class CategoryResponse {
    private Long id;

    private String categoryName;

    private String emoji;

    private CategoryType type;

    public static CategoryResponse from(Category category){
        return CategoryResponse.builder()
                .id(category.getId())
                .categoryName(category.getName())
                .emoji(category.getEmoji())
                .type(category.getType())
                .build();
    }
}
