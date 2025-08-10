package com.samazon.application.services;

import java.util.List;

import com.samazon.application.dto.CartDTO;

public interface CartService {
    public CartDTO addProductToCart(Long productId, Integer quantity);

    public List<CartDTO> getAllCarts();

    public CartDTO getCurrentUserCart();

    public CartDTO updateCartItemQuantity(Long productId, Integer quantity, String action);

    public void removeCartItem(Long productId);

    public List<Long> getAllCartIdsWithProduct(Long productId);

    public List<Long> getAllCartIdsWithCategory(Long categoryId);

    public void recalculateCartTotal(Long cartId);
}
