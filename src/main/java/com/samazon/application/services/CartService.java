package com.samazon.application.services;

import java.util.List;

import com.samazon.application.dto.carts.CartResponse;

import jakarta.transaction.Transactional;

public interface CartService {
    CartResponse addProductToCart(Long productId, Integer quantity);

    List<CartResponse> getAllCarts();

    List<Long> getAllCartIdsWithCategory(Long categoryId);

    List<Long> getAllCartIdsWithProduct(Long productId);

    CartResponse getCurrentUserCart();

    @Transactional
    CartResponse updateCartItemQuantity(Long productId, Integer quantity, String action);

    @Transactional
    void removeCartItem(Long productId);

    void recalculateCartTotal(Long cartId);

    @Transactional
    void clearCart(Long cartId);
}
