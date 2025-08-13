package com.samazon.application.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.samazon.application.dto.PagedResponse;
import com.samazon.application.dto.products.ProductRequest;
import com.samazon.application.dto.products.ProductResponse;
import com.samazon.application.exceptions.APIException;
import com.samazon.application.exceptions.ResourceNotFoundException;
import com.samazon.application.models.Category;
import com.samazon.application.models.Product;
import com.samazon.application.models.User;
import com.samazon.application.repositories.CartItemRepository;
import com.samazon.application.repositories.CategoryRepository;
import com.samazon.application.repositories.ProductRepository;
import com.samazon.application.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    private final CartService cartService;
    private final FileService fileService;

    private final ModelMapper modelMapper;

    @Value("${project.media.upload.dir}")
    private String mediaUploadDir;

    @Override
    public ProductResponse addProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getSellerId()));

        if (productRepository.existsByCategoryIdAndNameIgnoreCase(category.getId(), request.getName())) {
            throw new APIException("Product with name '" + request.getName() + "' already exists in category '"
                    + category.getCategoryName() + "'");
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .image(Optional.ofNullable(request.getImage()).orElse("default-product-image.png"))
                .stock(request.getStock())
                .price(request.getPrice())
                .discount(Optional.ofNullable(request.getDiscount()).orElse(0.0))
                .specialPrice(calculateSpecialPrice(request.getPrice(), request.getDiscount()))
                .category(category)
                .seller(seller)
                .build();
        Product createdProduct = productRepository.save(product);
        return modelMapper.map(createdProduct, ProductResponse.class);
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return modelMapper.map(product, ProductResponse.class);
    }

    @Override
    public PagedResponse<ProductResponse> getProducts(Long categoryId, String keyword, Integer page, Integer size,
            String sortBy, String sortOrder) {
        if (page < 0 || size <= 0) {
            throw new APIException("Invalid page or size parameters!");
        }
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(page, size, sortByAndOrder);
        Page<Product> productPage;
        if (keyword != null && !keyword.isEmpty() && categoryId != null) {
            productPage = productRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, keyword,
                    pageDetails);
            if (productPage.getContent().isEmpty()) {
                throw new APIException("No products found for the keyword: " + keyword + " in this category!");
            }
        } else if (keyword != null && !keyword.isEmpty()) {
            productPage = productRepository.findByNameContainingIgnoreCase(keyword, pageDetails);
            if (productPage.getContent().isEmpty()) {
                throw new APIException("No products found for the keyword: " + keyword);
            }
        } else if (categoryId != null) {
            productPage = productRepository.findByCategoryId(categoryId, pageDetails);
            if (productPage.getContent().isEmpty()) {
                throw new APIException("No products available for this category!");
            }
        } else {
            productPage = productRepository.findAll(pageDetails);
            if (productPage.getContent().isEmpty()) {
                throw new APIException("No more products available!");
            }
        }
        List<ProductResponse> responses = productPage.getContent().stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .toList();
        return PagedResponse.<ProductResponse>builder()
                .content(responses)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .lastPage(productPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public ProductResponse patchProduct(Long productId, ProductRequest request) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Long targetCategoryId = request.getCategoryId() != null
                ? request.getCategoryId()
                : existingProduct.getCategory().getId();
        Category category = categoryRepository.findById(targetCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        String newName = request.getName() != null ? request.getName() : existingProduct.getName();
        if (productRepository.existsByCategoryIdAndNameIgnoreCaseAndIdNot(category.getId(), newName, productId)) {
            throw new APIException("Product with name '" + newName + "' already exists in category '"
                    + category.getCategoryName() + "'");
        }

        // Apply only provided fields
        existingProduct.setCategory(category);
        if (request.getName() != null)
            existingProduct.setName(request.getName());
        if (request.getDescription() != null)
            existingProduct.setDescription(request.getDescription());
        if (request.getImage() != null)
            existingProduct.setImage(request.getImage());
        if (request.getStock() != null)
            existingProduct.setStock(request.getStock());
        if (request.getPrice() != null)
            existingProduct.setPrice(request.getPrice());
        if (request.getDiscount() != null)
            existingProduct.setDiscount(request.getDiscount());

        Double effectiveDiscount = existingProduct.getDiscount();
        if (effectiveDiscount == null || effectiveDiscount == 0) {
            existingProduct.setSpecialPrice(null);
        } else {
            existingProduct.setSpecialPrice(
                    calculateSpecialPrice(existingProduct.getPrice(), effectiveDiscount));
        }

        Product updated = productRepository.save(existingProduct);
        cartService.getAllCartIdsWithProduct(productId).forEach(cartService::recalculateCartTotal);
        return modelMapper.map(updated, ProductResponse.class);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // disallow changing seller
        if (!existingProduct.getSeller().getId().equals(request.getSellerId())) {
            throw new APIException("Cannot change seller of a product once created!");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        if (productRepository.existsByCategoryIdAndNameIgnoreCaseAndIdNot(category.getId(), request.getName(),
                productId)) {
            throw new APIException("Product with name '" + request.getName() + "' already exists in category '"
                    + category.getCategoryName() + "'");
        }
        existingProduct.setCategory(category);

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setImage(Optional.ofNullable(request.getImage()).orElse("default-product-image.png"));
        existingProduct.setStock(request.getStock());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setDiscount(Optional.ofNullable(request.getDiscount()).orElse(0.0));
        existingProduct.setSpecialPrice(calculateSpecialPrice(request.getPrice(), request.getDiscount()));

        Product updatedProduct = productRepository.save(existingProduct);
        cartService.getAllCartIdsWithProduct(productId).forEach(cartId -> cartService.recalculateCartTotal(cartId));
        return modelMapper.map(updatedProduct, ProductResponse.class);
    }

    @Override
    public ProductResponse updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileService.uploadMedia(mediaUploadDir, image);
            product.setImage(imageUrl);
        }
        Product updatedProduct = productRepository.save(product);
        return modelMapper.map(updatedProduct, ProductResponse.class);
    }

    @Override
    @Transactional
    public Void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id",
                        productId));

        List<Long> cartIds = cartService.getAllCartIdsWithProduct(productId);

        cartItemRepository.deleteAllByProductId(productId);

        productRepository.delete(product);

        // Recalculate totals after removal
        cartIds.forEach(cartService::recalculateCartTotal);
        return null;
    }

    private Double calculateSpecialPrice(Double price, Double discount) {
        if (discount == null || discount == 0) {
            return null;
        }
        return ((int) ((price - (price * (discount / 100))) * 100.0)) / 100.0;
    }
}
