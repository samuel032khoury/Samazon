package com.samazon.application.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.samazon.application.dto.carts.CartItemRequest;
import com.samazon.application.dto.carts.CartResponse;
import com.samazon.application.dto.requests.UpdateCartItemQuantityRequest;
import com.samazon.application.services.CartService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/admin/cart-audit")
    public ResponseEntity<List<CartResponse>> getAllCarts() {
        List<CartResponse> responses = cartService.getAllCarts();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/user/cart")
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody CartItemRequest request) {
        CartResponse response = cartService.addProductToCart(request.getProductId(),
                request.getQuantity());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/cart")
    public ResponseEntity<CartResponse> getCurrentUserCart() {
        CartResponse response = cartService.getCurrentUserCart();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/user/cart/item")
    public ResponseEntity<CartResponse> updateCartItemQuantity(@RequestParam Long productId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request) {
        CartResponse response = cartService.updateCartItemQuantity(productId, request.getQuantity(),
                request.getAction());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/cart/item")
    public ResponseEntity<Void> removeCartItem(@RequestParam Long productId) {
        cartService.removeCartItem(productId);
        return ResponseEntity.noContent().build();
    }
}