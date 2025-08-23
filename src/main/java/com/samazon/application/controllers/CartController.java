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
import com.samazon.application.models.User;
import com.samazon.application.dto.carts.CartItemUpdateQuantityRequest;
import com.samazon.application.services.CartService;
import com.samazon.application.utils.AuthUtil;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CartController {

    private final AuthUtil authUtil;

    private final CartService cartService;

    @PostMapping("/user/cart")
    public ResponseEntity<CartResponse> addCartItemToUserCart(@Valid @RequestBody CartItemRequest request) {
        Long cartId = authUtil.getCurrentUser().getCart().getId();
        CartResponse response = cartService.addCartItemToCart(cartId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/audit/carts")
    public ResponseEntity<List<CartResponse>> getAllCarts() {
        List<CartResponse> responses = cartService.getAllCarts();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/cart")
    public ResponseEntity<CartResponse> getUserCart() {
        User user = authUtil.getCurrentUser();
        CartResponse response = cartService.getCartByUser(user);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/user/cart/items")
    public ResponseEntity<CartResponse> updateUserCartItemQuantity(@RequestParam Long productId,
            @Valid @RequestBody CartItemUpdateQuantityRequest request) {
        Long cartId = authUtil.getCurrentUser().getCart().getId();
        CartResponse response = cartService.updateCartItemQuantity(cartId, productId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/cart/items")
    public ResponseEntity<Void> removeProductFromUserCart(@RequestParam Long productId) {
        Long cartId = authUtil.getCurrentUser().getCart().getId();
        cartService.removeProductFromCart(cartId, productId);
        return ResponseEntity.noContent().build();
    }

}