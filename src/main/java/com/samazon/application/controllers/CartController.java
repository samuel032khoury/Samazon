package com.samazon.application.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samazon.application.dto.CartDTO;
import com.samazon.application.dto.requests.AddCartItemRequestDTO;
import com.samazon.application.services.CartService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/carts")
@AllArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(@Valid @RequestBody AddCartItemRequestDTO cartItemDTO) {
        CartDTO cartDTO = cartService.addProductToCart(cartItemDTO.getProductId(), cartItemDTO.getQuantity());
        return ResponseEntity.ok(cartDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        List<CartDTO> cartDTOs = cartService.getAllCarts();
        return ResponseEntity.ok(cartDTOs);
    }

    @GetMapping("/user")
    public ResponseEntity<CartDTO> getCurrentUserCart() {
        CartDTO cartDTO = cartService.getCurrentUserCart();
        return ResponseEntity.ok(cartDTO);
    }
}