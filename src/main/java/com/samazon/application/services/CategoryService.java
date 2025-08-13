package com.samazon.application.services;

import com.samazon.application.dto.PagedResponse;
import com.samazon.application.dto.categories.CategoryRequest;
import com.samazon.application.dto.categories.CategoryResponse;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);

    PagedResponse<CategoryResponse> getAllCategories(Integer page, Integer size, String sortBy, String sortOrder);

    CategoryResponse updateCategory(Long categoryId, CategoryRequest request);

    void deleteCategory(Long categoryId);
}
