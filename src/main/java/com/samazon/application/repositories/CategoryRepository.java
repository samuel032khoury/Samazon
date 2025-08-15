package com.samazon.application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.samazon.application.models.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    public boolean existsByCategoryName(String categoryName);
}
