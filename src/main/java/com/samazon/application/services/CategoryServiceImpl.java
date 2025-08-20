package com.samazon.application.services;

import java.util.Collections;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.samazon.application.dto.categories.CategoryRequest;
import com.samazon.application.dto.categories.CategoryResponse;
import com.samazon.application.dto.common.PagedResponse;
import com.samazon.application.events.CategoryDeletedEvent;
import com.samazon.application.exceptions.APIException;
import com.samazon.application.exceptions.ResourceNotFoundException;
import com.samazon.application.models.Category;
import com.samazon.application.repositories.CategoryRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final ModelMapper modelMapper;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = modelMapper.map(request, Category.class);
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new APIException("Category with this name already exists!");
        }
        Category createdCategory = categoryRepository.save(category);
        return modelMapper.map(createdCategory, CategoryResponse.class);
    }

    @Override
    public PagedResponse<CategoryResponse> getAllCategories(Integer page, Integer size, String sortBy,
            String sortOrder) {
        if (page < 0 || size <= 0) {
            throw new APIException("Invalid page or size parameters!");
        }

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(page, size, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();
        if (categories.isEmpty()) {
            return new PagedResponse<>(
                    Collections.emptyList(),
                    categoryPage.getNumber(),
                    categoryPage.getSize(),
                    categoryPage.getTotalElements(),
                    categoryPage.getTotalPages(),
                    categoryPage.isLast());
        }
        List<CategoryResponse> responses = categories.stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .toList();
        return PagedResponse.<CategoryResponse>builder()
                .content(responses)
                .pageNumber(categoryPage.getNumber())
                .pageSize(categoryPage.getSize())
                .totalElements(categoryPage.getTotalElements())
                .totalPages(categoryPage.getTotalPages())
                .lastPage(categoryPage.isLast())
                .build();
    }

    @Override
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        if (categoryRepository.existsByCategoryName(request.getCategoryName())
                && !existingCategory.getCategoryName().equals(request.getCategoryName())) {
            throw new APIException("Category with this name already exists!");
        }
        modelMapper.map(request, existingCategory);
        Category updatedCategory = categoryRepository.save(existingCategory);
        return modelMapper.map(updatedCategory, CategoryResponse.class);
    }

    @Override
    @Transactional
    public Void deleteCategory(Long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        categoryRepository.deleteById(categoryId);
        eventPublisher.publishEvent(new CategoryDeletedEvent(this, categoryId));
        return null;
    }

}
