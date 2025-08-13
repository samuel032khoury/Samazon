package com.samazon.application.controllers;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.samazon.application.config.AppConstants;
import com.samazon.application.dto.PagedResponse;
import com.samazon.application.dto.products.ProductRequest;
import com.samazon.application.dto.products.ProductResponse;
import com.samazon.application.services.ProductService;
import com.samazon.application.validation.OnCreateOrUpdate;
import com.samazon.application.validation.OnPatch;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("products")
    public ResponseEntity<ProductResponse> addProduct(
            @RequestBody @Validated(OnCreateOrUpdate.class) ProductRequest request) {
        ProductResponse createdProductResponse = productService.addProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProductResponse);
    }

    @GetMapping("/public/products/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) {
        ProductResponse productResponse = productService.getProductById(productId);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/public/products")
    public ResponseEntity<PagedResponse<ProductResponse>> getProducts(
            @RequestParam(name = "category", required = false) Long categoryId,
            @RequestParam(name = "search", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = AppConstants.PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", defaultValue = AppConstants.PAGE_SIZE) Integer size,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {
        PagedResponse<ProductResponse> products = productService.getProducts(categoryId, keyword, page, size, sortBy,
                sortOrder);
        return ResponseEntity.ok(products);
    }

    @PatchMapping("/products/{productId}")
    public ResponseEntity<ProductResponse> patchProduct(
            @PathVariable Long productId,
            @RequestBody @Validated(OnPatch.class) ProductRequest request) {
        ProductResponse patchedProductResponse = productService.patchProduct(productId, request);
        return ResponseEntity.ok(patchedProductResponse);
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long productId,
            @RequestBody @Validated(OnCreateOrUpdate.class) ProductRequest request) {
        ProductResponse updatedProductResponse = productService.updateProduct(productId, request);
        return ResponseEntity.ok(updatedProductResponse);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductResponse> updateProductImage(@PathVariable Long productId,
            @RequestParam("image") MultipartFile image) throws IOException {
        ProductResponse updatedProductResponse = productService.updateProductImage(productId, image);
        return ResponseEntity.ok(updatedProductResponse);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

}
