package com.samazon.application.services;

import com.samazon.application.dto.PagedResponse;
import com.samazon.application.dto.categories.CategoryRequest;
import com.samazon.application.dto.categories.CategoryResponse;

public interface CategoryService {
    PagedResponse<CategoryResponse> getAllCategories(Integer page, Integer size, String sortBy, String sortOrder);

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse deleteCategory(Long categoryId);

    CategoryResponse updateCategory(Long categoryId, CategoryRequest request);
}
