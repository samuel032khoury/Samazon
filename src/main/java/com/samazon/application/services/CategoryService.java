package com.samazon.application.services;

import java.util.List;

import com.samazon.application.models.Category;

public interface CategoryService {
    List<Category> getAllCategories();

    void createCategory(Category category);

    void deleteCategory(Long categoryId);
}
