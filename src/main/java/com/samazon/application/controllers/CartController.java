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
import com.samazon.application.dto.requests.UpdateCartItemRequestDTO;
import com.samazon.application.services.CartService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/admin/all-carts")
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        List<CartDTO> cartDTOs = cartService.getAllCarts();
        return ResponseEntity.ok(cartDTOs);
    }

    @PostMapping("/cart")
    public ResponseEntity<CartDTO> addToCart(@Valid @RequestBody AddCartItemRequestDTO cartItemDTO) {
        CartDTO cartDTO = cartService.addProductToCart(cartItemDTO.getProductId(), cartItemDTO.getQuantity());
        return ResponseEntity.ok(cartDTO);
    }

    @GetMapping("/cart")
    public ResponseEntity<CartDTO> getCurrentUserCart() {
        CartDTO cartDTO = cartService.getCurrentUserCart();
        return ResponseEntity.ok(cartDTO);
    }

    @PatchMapping("/cart")
    public ResponseEntity<CartDTO> updateCartItem(@Valid @RequestBody UpdateCartItemRequestDTO cartItemDTO) {
        CartDTO cartDTO = cartService.updateCartItem(cartItemDTO.getProductId(), cartItemDTO.getQuantity(),
                cartItemDTO.getAction());
        return ResponseEntity.ok(cartDTO);
    }

    @DeleteMapping("/cart")
    public ResponseEntity<Void> removeCartItem(@RequestParam Long productId) {
        cartService.removeCartItem(productId);
        return ResponseEntity.noContent().build();
    }
}