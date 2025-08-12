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

import com.samazon.application.dto.CartDTO;
import com.samazon.application.dto.requests.AddCartItemRequestDTO;
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
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        List<CartDTO> cartDTOs = cartService.getAllCarts();
        return ResponseEntity.ok(cartDTOs);
    }

    @PostMapping("/user/cart")
    public ResponseEntity<CartDTO> addToCart(@Valid @RequestBody AddCartItemRequestDTO request) {
        CartDTO cartDTO = cartService.addProductToCart(request.getProductId(),
                request.getQuantity());
        return ResponseEntity.ok(cartDTO);
    }

    @GetMapping("/user/cart")
    public ResponseEntity<CartDTO> getCurrentUserCart() {
        CartDTO cartDTO = cartService.getCurrentUserCart();
        return ResponseEntity.ok(cartDTO);
    }

    @PatchMapping("/user/cart/item")
    public ResponseEntity<CartDTO> updateCartItemQuantity(@RequestParam Long productId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request) {
        CartDTO cartDTO = cartService.updateCartItemQuantity(productId, request.getQuantity(),
                request.getAction());
        return ResponseEntity.ok(cartDTO);
    }

    @DeleteMapping("/user/cart/item")
    public ResponseEntity<Void> removeCartItem(@RequestParam Long productId) {
        cartService.removeCartItem(productId);
        return ResponseEntity.noContent().build();
    }
}