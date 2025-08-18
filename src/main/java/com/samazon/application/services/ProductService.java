package com.samazon.application.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.samazon.application.dto.common.PagedResponse;
import com.samazon.application.dto.products.ProductRequest;
import com.samazon.application.dto.products.ProductResponse;

public interface ProductService {

    ProductResponse addProduct(ProductRequest request);

    PagedResponse<ProductResponse> getProducts(Long categoryId, String keyword, Integer page, Integer size,
            String sortBy, String sortOrder);

    ProductResponse getProductById(Long productId);

    ProductResponse patchProduct(Long productId, ProductRequest productRequest);

    ProductResponse updateProduct(Long productId, ProductRequest productRequest);

    ProductResponse updateProductImage(Long productId, MultipartFile image) throws IOException;

    Void deleteProduct(Long productId);
}
