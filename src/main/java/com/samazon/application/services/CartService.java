package com.samazon.application.services;

import java.util.List;

import com.samazon.application.dto.carts.CartResponse;

public interface CartService {
    CartResponse addProductToCart(Long productId, Integer quantity);

    List<CartResponse> getAllCarts();

    CartResponse getCurrentUserCart();

    CartResponse updateCartItemQuantity(Long productId, Integer quantity, String action);

    void removeCartItem(Long productId);

    List<Long> getAllCartIdsWithProduct(Long productId);

    List<Long> getAllCartIdsWithCategory(Long categoryId);

    void recalculateCartTotal(Long cartId);

    void clearCart(Long id);
}
