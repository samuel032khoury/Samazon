package com.samazon.application.services;

import java.io.IOException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.samazon.application.dto.ProductDTO;
import com.samazon.application.dto.responses.ProductResponse;
import com.samazon.application.exceptions.APIException;
import com.samazon.application.exceptions.ResourceNotFoundException;
import com.samazon.application.models.Category;
import com.samazon.application.models.Product;
import com.samazon.application.repositories.CategoryRepository;
import com.samazon.application.repositories.ProductRepository;
import com.samazon.application.utils.PatchUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartService cartService;
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
    public ProductDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer page, Integer size, String sortBy, String sortOrder) {
        if (page < 0 || size <= 0) {
            throw new APIException("Invalid page or size parameters!");
        }
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(page, size, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) {
            throw new APIException("No more products available!");
        }
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        return ProductResponse.builder()
                .content(productDTOs)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .lastPage(productPage.isLast())
                .build();
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        Product product = category.getProducts().stream()
                .filter(p -> p.getName().equalsIgnoreCase(productDTO.getName()))
                .findFirst()
                .orElse(null);
        if (product != null) {
            throw new APIException("Product with name '" + productDTO.getName() + "' already exists in this category");
        } else {
            product = modelMapper.map(productDTO, Product.class);
            product.setCategory(category);
        }
        if (product.getDiscount() == null)
            product.setDiscount(0.0);
        if (product.getStock() == null)
            product.setStock(0);
        if (product.getImage() == null || product.getImage().isEmpty())
            product.setImage("default-product-image.png");
        product.setSpecialPrice(calculateSpecialPrice(product));
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId, Integer page, Integer size, String sortBy,
            String sortOrder) {
        if (page < 0 || size <= 0) {
            throw new APIException("Invalid page or size parameters!");
        }
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(page, size, sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageDetails);
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) {
            throw new APIException("No products available for this category!");
        }
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        return ProductResponse.builder()
                .content(productDTOs)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .lastPage(productPage.isLast())
                .build();
    }

    @Override
    public ProductResponse searchProductsByKeyword(String keyword, Integer page, Integer size, String sortBy,
            String sortOrder) {
        if (page < 0 || size <= 0) {
            throw new APIException("Invalid page or size parameters!");
        }
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(page, size, sortByAndOrder);

        Page<Product> productPage = productRepository.findByNameContainingIgnoreCase(keyword, pageDetails);
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) {
            throw new APIException("No products found for the keyword: " + keyword);
        }
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        return ProductResponse.builder()
                .content(productDTOs)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .lastPage(productPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        modelMapper.map(productDTO, existingProduct);
        existingProduct.setId(productId);
        if (existingProduct.getDiscount() == null)
            existingProduct.setDiscount(0.0);
        if (existingProduct.getStock() == null)
            existingProduct.setStock(0);
        if (existingProduct.getImage() == null || existingProduct.getImage().isEmpty())
            existingProduct.setImage("default-product-image.png");
        existingProduct.setSpecialPrice(calculateSpecialPrice(existingProduct));
        Product updatedProduct = productRepository.save(existingProduct);
        // cartService.updateAllCartsWithProduct(productId);
        cartService.getAllCartIdsWithProduct(productId).forEach(cartId -> cartService.recalculateCartTotal(cartId));
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    @Transactional
    public ProductDTO patchProduct(Long productId, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        PatchUtil.patchNonNullFields(modelMapper.map(productDTO, Product.class), existingProduct);
        existingProduct.setId(productId);
        existingProduct.setSpecialPrice(calculateSpecialPrice(existingProduct));

        Product updatedProduct = productRepository.save(existingProduct);
        cartService.getAllCartIdsWithProduct(productId).forEach(cartId -> cartService.recalculateCartTotal(cartId));
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
    @Transactional
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        List<Long> cartIds = cartService.getAllCartIdsWithProduct(productId);
        productRepository.deleteById(productId);
        cartIds.forEach(cartId -> cartService.recalculateCartTotal(cartId));
        return modelMapper.map(product, ProductDTO.class);
    }

}
