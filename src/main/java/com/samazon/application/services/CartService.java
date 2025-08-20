package com.samazon.application.services;

import java.util.List;

import com.samazon.application.dto.carts.CartResponse;

import jakarta.transaction.Transactional;

public interface CartService {
    CartResponse addProductToCart(Long productId, Integer quantity);

    List<CartResponse> getAllCarts();

    CartResponse getCurrentUserCart();

    @Transactional
    CartResponse updateCartItemQuantity(Long productId, Integer quantity, String action);

    @Transactional
    void removeCartItem(Long productId);

    @Transactional
    void clearCart(Long cartId);
}
