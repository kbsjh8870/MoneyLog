package org.example.backend.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.category.dto.CategoryRequest;
import org.example.backend.category.dto.CategoryResponse;
import org.example.backend.category.service.CategoryService;
import org.example.backend.common.response.ApiResponse;
import org.example.backend.common.response.PageMeta;
import org.example.backend.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories(CategoryRequest request,
                                                                             @PageableDefault(size = 5)Pageable pageable,
                                                                             @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Page<CategoryResponse> categories = categoryService.getCategories(customUserDetails.getUserId(), request.getType(), pageable);

        return ResponseEntity.ok(ApiResponse.success(request.getType()+" 카테고리 목록 조회 완료 ",categories.getContent(), PageMeta.from(categories)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> addCategory(@Valid @RequestBody CategoryRequest request,
                                                                     @AuthenticationPrincipal CustomUserDetails customUserDetails){
        CategoryResponse newCategory = categoryService.addCategory(customUserDetails.getUserId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("카테고리 추가 완료",newCategory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id,
                                                                        @RequestBody CategoryRequest request,
                                                                        @AuthenticationPrincipal CustomUserDetails customUserDetails){
        CategoryResponse updatedCategory = categoryService.updateCategory(customUserDetails.getUserId(), id, request);

        return ResponseEntity.ok(ApiResponse.success(id + "카테고리 수정 완료",updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id,
                                                            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        categoryService.deleteCategory(customUserDetails.getUserId(), id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(id + " 카테고리 삭제 완료",null));
    }
}
