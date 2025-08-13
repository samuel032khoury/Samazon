package com.samazon.application.services;

import com.samazon.application.dto.categories.CategoryRequest;
import com.samazon.application.dto.categories.CategoryResponse;
import com.samazon.application.dto.common.PagedResponse;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);

    PagedResponse<CategoryResponse> getAllCategories(Integer page, Integer size, String sortBy, String sortOrder);

    CategoryResponse updateCategory(Long categoryId, CategoryRequest request);

    Void deleteCategory(Long categoryId);
}
