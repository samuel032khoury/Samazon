package com.samazon.application.services;

import java.util.List;

import com.samazon.application.dto.carts.CartItemRequest;
import com.samazon.application.dto.carts.CartItemUpdateQuantityRequest;
import com.samazon.application.dto.carts.CartResponse;
import com.samazon.application.models.User;

import jakarta.transaction.Transactional;

public interface CartService {
    CartResponse addCartItemToCart(Long cartId, CartItemRequest request);

    List<CartResponse> getAllCarts();

    CartResponse getCartByUser(User user);

    @Transactional
    CartResponse updateCartItemQuantity(Long cartId, Long productId, CartItemUpdateQuantityRequest request);

    @Transactional
    void removeProductFromCart(Long cartId, Long productId);

    @Transactional
    void clearCart(Long cartId);
}
