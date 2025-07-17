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
import com.samazon.application.dto.ProductDTO;
import com.samazon.application.dto.responses.ProductResponse;
import com.samazon.application.services.ProductService;
import com.samazon.application.validation.OnCreateOrUpdate;
import com.samazon.application.validation.OnPatch;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "page", defaultValue = AppConstants.PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", defaultValue = AppConstants.PAGE_SIZE) Integer size,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {
        ProductResponse products = productService.getAllProducts(page, size, sortBy, sortOrder);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody @Validated(OnCreateOrUpdate.class) ProductDTO productDTO,
            @PathVariable Long categoryId) {
        ProductDTO savedProductDTO = productService.addProduct(productDTO, categoryId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedProductDTO);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId,
            @RequestParam(name = "page", defaultValue = AppConstants.PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", defaultValue = AppConstants.PAGE_SIZE) Integer size,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {
        ProductResponse products = productService.getProductsByCategory(categoryId, page, size, sortBy, sortOrder);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/public/products/search")
    public ResponseEntity<ProductResponse> searchProductsByKeyword(@RequestParam("k") String keyword,
            @RequestParam(name = "page", defaultValue = AppConstants.PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", defaultValue = AppConstants.PAGE_SIZE) Integer size,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {
        ProductResponse products = productService.searchProductsByKeyword(keyword, page, size, sortBy, sortOrder);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/admin/product/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId,
            @RequestBody @Validated(OnCreateOrUpdate.class) ProductDTO productDTO) {
        ProductDTO updatedProductDTO = productService.updateProduct(productId, productDTO);
        return ResponseEntity.ok(updatedProductDTO);
    }

    @PatchMapping("/admin/product/{productId}")
    public ResponseEntity<ProductDTO> patchProduct(@PathVariable Long productId,
            @RequestBody @Validated(OnPatch.class) ProductDTO productDTO) {
        System.out.println("Patching product with ID: " + productId);
        System.out.println("Patch data: " + productDTO.toString());
        ProductDTO patchedProductDTO = productService.patchProduct(productId, productDTO);
        return ResponseEntity.ok(patchedProductDTO);
    }

    @PutMapping("/admin/product/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
            @RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO updatedProductDTO = productService.updateProductImage(productId, image);
        return ResponseEntity.ok(updatedProductDTO);
    }

    @DeleteMapping("/admin/product/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        ProductDTO deletedProductDTO = productService.deleteProduct(productId);
        return ResponseEntity.ok(deletedProductDTO);
    }

}
