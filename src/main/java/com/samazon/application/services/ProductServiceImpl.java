package com.samazon.application.services;

import java.io.IOException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.samazon.application.dto.ProductDTO;
import com.samazon.application.exceptions.ResourceNotFoundException;
import com.samazon.application.models.Category;
import com.samazon.application.models.Product;
import com.samazon.application.repositories.CategoryRepository;
import com.samazon.application.repositories.ProductRepository;
import com.samazon.application.responses.ProductResponse;
import com.samazon.utils.PatchUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;

    @Value("${project.media.upload.dir}")
    private String mediaUploadDir;

    private Double calculateSpecialPrice(Product product) {
        if (product.getDiscount() == 0) {
            return null;
        }
        return ((int) ((product.getPrice() - (product.getPrice() * (product.getDiscount() / 100))) * 100.0)) / 100.0;
    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> categoryDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        return ProductResponse.builder()
                .content(categoryDTOs)
                .build();
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Product product = modelMapper.map(productDTO, Product.class);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        product.setCategory(category);
        if (product.getDiscount() == null)
            product.setDiscount(0.0);
        if (product.getQuantity() == null)
            product.setQuantity(0);
        if (product.getImage() == null || product.getImage().isEmpty())
            product.setImage("default-product-image.png");
        product.setSpecialPrice(calculateSpecialPrice(product));
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        return ProductResponse.builder()
                .content(productDTOs)
                .build();
    }

    @Override
    public ProductResponse searchProductsByKeyword(String keyword) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        return ProductResponse.builder()
                .content(productDTOs)
                .build();
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        modelMapper.map(productDTO, existingProduct);
        existingProduct.setId(productId);
        if (existingProduct.getDiscount() == null)
            existingProduct.setDiscount(0.0);
        if (existingProduct.getQuantity() == null)
            existingProduct.setQuantity(0);
        if (existingProduct.getImage() == null || existingProduct.getImage().isEmpty())
            existingProduct.setImage("default-product-image.png");
        existingProduct.setSpecialPrice(calculateSpecialPrice(existingProduct));
        Product updatedProduct = productRepository.save(existingProduct);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO patchProduct(Long productId, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        PatchUtil.patchNonNullFields(modelMapper.map(productDTO, Product.class), existingProduct);
        existingProduct.setId(productId);
        existingProduct.setSpecialPrice(calculateSpecialPrice(existingProduct));

        Product updatedProduct = productRepository.save(existingProduct);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileService.uploadMedia(mediaUploadDir, image);
            product.setImage(imageUrl);
        }
        Product updatedProduct = productRepository.save(product);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        productRepository.deleteById(productId);
        return modelMapper.map(product, ProductDTO.class);
    }

}
