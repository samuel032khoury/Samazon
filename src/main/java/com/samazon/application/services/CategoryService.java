package com.samazon.application.services;

import com.samazon.application.responses.CategoryDTO;
import com.samazon.application.responses.CategoryResponse;

public interface CategoryService {
    CategoryResponse getAllCategories(Integer page, Integer size, String sortBy, String sortOrder);

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(Long categoryId, CategoryDTO category);
}
