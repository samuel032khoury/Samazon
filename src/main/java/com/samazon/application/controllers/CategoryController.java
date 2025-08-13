package com.samazon.application.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.samazon.application.config.AppConstants;
import com.samazon.application.dto.PagedResponse;
import com.samazon.application.dto.categories.CategoryRequest;
import com.samazon.application.dto.categories.CategoryResponse;
import com.samazon.application.services.CategoryService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request) {
        CategoryResponse createdCategoryResponse = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategoryResponse);
    }

    @GetMapping("/public/categories")
    public ResponseEntity<PagedResponse<CategoryResponse>> getAllCategories(
            @RequestParam(name = "page", defaultValue = AppConstants.PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", defaultValue = AppConstants.PAGE_SIZE) Integer size,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {
        return ResponseEntity.ok(categoryService.getAllCategories(page, size, sortBy, sortOrder));
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long categoryId,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse updatedCategoryResponse = categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(updatedCategoryResponse);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

}