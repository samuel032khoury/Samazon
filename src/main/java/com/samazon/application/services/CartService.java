package com.samazon.application.services;

import java.util.List;

import com.samazon.application.dto.carts.CartResponse;
import com.samazon.application.models.User;

public interface CartService {
    public CartResponse addProductToCart(Long productId, Integer quantity);

    public List<CartResponse> getAllCarts();

    public CartResponse getCurrentUserCart();

    public CartResponse updateCartItemQuantity(Long productId, Integer quantity, String action);

    public void removeCartItem(Long productId);

    public List<Long> getAllCartIdsWithProduct(Long productId);

    public List<Long> getAllCartIdsWithCategory(Long categoryId);

    public void recalculateCartTotal(Long cartId);

    public void clearCart(Long id);

    public void createCartForUser(User newUser);
}
