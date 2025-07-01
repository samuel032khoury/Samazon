package com.samazon.application.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.samazon.application.dto.ProductDTO;
import com.samazon.application.responses.ProductResponse;

public interface ProductService {

    ProductResponse getAllProducts();

    ProductDTO addProduct(ProductDTO productDTO, Long categoryId);

    ProductResponse getProductsByCategory(Long categoryId);

    ProductResponse searchProductsByKeyword(String keyword);

    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    ProductDTO deleteProduct(Long productId);

    ProductDTO patchProduct(Long productId, ProductDTO productDTO);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

}
