package com.samazon.application.services;

import com.samazon.application.dto.CartDTO;

public interface CartService {
    public CartDTO addProductToCart(Long productId, Integer quantity);
}
