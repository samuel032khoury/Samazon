package com.samazon.application.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.samazon.application.models.Category;
import com.samazon.application.repositories.CategoryRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category with this name already exists!");
        }
        categoryRepository.save(category);
    }

    @Override
    public Category deleteCategory(Long categoryId) throws ResponseStatusException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found!"));
        categoryRepository.deleteById(categoryId);
        return category;
    }

    @Override
    public Category updateCategory(Long categoryId, Category category) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found!");
        }
        category.setCategoryId(categoryId);
        categoryRepository.save(category);
        return category;
    }

}
