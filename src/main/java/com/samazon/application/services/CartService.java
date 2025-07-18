package com.samazon.application.services;

import java.util.List;

import com.samazon.application.dto.CartDTO;

public interface CartService {
    public CartDTO addProductToCart(Long productId, Integer quantity);

    public List<CartDTO> getAllCarts();

    public CartDTO getCurrentUserCart();

}
