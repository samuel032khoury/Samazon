package com.samazon.application.services;

import java.util.List;

import com.samazon.application.dto.carts.CartResponse;

import jakarta.transaction.Transactional;

public interface CartService {
    CartResponse addProductToUserCart(Long productId, Integer quantity);

    List<CartResponse> getAllCarts();

    CartResponse getUserCart();

    @Transactional
    CartResponse updateProductQuantityInUserCart(Long productId, Integer quantity, String action);

    @Transactional
    void removeProductFromUserCart(Long productId);

    @Transactional
    void clearCart(Long cartId);
}
