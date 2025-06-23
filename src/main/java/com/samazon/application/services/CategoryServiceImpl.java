package com.samazon.application.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.samazon.application.exceptions.APIException;
import com.samazon.application.exceptions.ResourceNotFoundException;
import com.samazon.application.models.Category;
import com.samazon.application.repositories.CategoryRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        if (categoryRepository.count() == 0) {
            throw new APIException("Category list is empty!");
        }
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new APIException("Category with this name already exists!");
        }
        categoryRepository.save(category);
    }

    @Override
    public Category deleteCategory(Long categoryId) throws ResponseStatusException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        categoryRepository.deleteById(categoryId);
        return category;
    }

    @Override
    public Category updateCategory(Long categoryId, Category category) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "categoryId", categoryId);
        }
        if (categoryRepository.existsByCategoryName(category.getCategoryName())
                && !categoryRepository.findById(categoryId).get().getCategoryName()
                        .equals(category.getCategoryName())) {
            throw new APIException("Category with this name already exists!");
        }
        category.setCategoryId(categoryId);
        categoryRepository.save(category);
        return category;
    }

}
