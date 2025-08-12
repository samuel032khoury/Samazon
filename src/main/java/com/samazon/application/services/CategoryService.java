package com.samazon.application.services;

import com.samazon.application.dto.CategoryDTO;
import com.samazon.application.dto.responses.CategoryResponse;
import com.samazon.application.dto.responses.PagedResponse;

public interface CategoryService {
    PagedResponse<CategoryResponse> getAllCategories(Integer page, Integer size, String sortBy, String sortOrder);

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(Long categoryId, CategoryDTO category);
}
