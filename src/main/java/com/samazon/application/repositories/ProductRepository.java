package com.samazon.application.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.samazon.application.models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageDetails);

    Boolean existsByCategoryIdAndNameIgnoreCase(Long id, String name);

    Page<Product> findByCategoryIdAndNameContainingIgnoreCase(Long categoryId, String keyword,
            Pageable pageDetails);

    Boolean existsByCategoryIdAndNameIgnoreCaseAndIdNot(Long id, String name, Long productId);

}
