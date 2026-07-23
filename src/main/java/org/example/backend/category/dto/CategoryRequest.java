package org.example.backend.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.example.backend.category.entity.CategoryType;

@Getter
public class CategoryRequest {
    @NotBlank(message = "카테고리 이름을 입력해주세요.")
    private String categoryName;

    @Size(max = 10)
    private String emoji;

    @NotNull(message = "지출/수입 유형을 선택해주세요.")
    private CategoryType type;
}
