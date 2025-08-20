package com.samazon.application.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.samazon.application.dto.carts.CartResponse;
import com.samazon.application.events.CartItemChangedEvent;
import com.samazon.application.exceptions.APIException;
import com.samazon.application.exceptions.ResourceNotFoundException;
import com.samazon.application.models.Cart;
import com.samazon.application.models.CartItem;
import com.samazon.application.models.Product;
import com.samazon.application.repositories.CartItemRepository;
import com.samazon.application.repositories.CartRepository;
import com.samazon.application.repositories.ProductRepository;
import com.samazon.application.utils.AuthUtil;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    private final AuthUtil authUtil;
    private final ApplicationEventPublisher eventPublisher;

    private final ModelMapper modelMapper;

    @Override
    public CartResponse addProductToCart(Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(authUtil.getCurrentUser().getId())
                .orElseThrow(() -> new APIException("Cart not found for user: " + authUtil.getCurrentUser().getId()));

        // Retrieve Product Details
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (existingCartItem.isPresent()) {
            // if cartItem exists, update its quantity
            CartItem cartItem = existingCartItem.get();
            validateStockAvailability(product, cartItem.getQuantity() + quantity, "increment");
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            validateStockAvailability(product, quantity, "set");
            // Create CartItem and save it
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .unitPriceAtAddToCart(
                            product.getSpecialPrice() != null ? product.getSpecialPrice() : product.getPrice())
                    .build();
            cartItemRepository.save(cartItem);
            cart.getCartItems().add(cartItem);
        }

        eventPublisher.publishEvent(new CartItemChangedEvent(this, cart.getId()));
        return modelMapper.map(cart, CartResponse.class);
    }

    @Override
    public List<CartResponse> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        return carts.stream()
                .map(cart -> modelMapper.map(cart, CartResponse.class))
                .toList();
    }

    @Override
    public CartResponse getCurrentUserCart() {
        Cart userCart = cartRepository.findByUserId(authUtil.getCurrentUser().getId())
                .orElseThrow(() -> new APIException("Cart not found for user: " + authUtil.getCurrentUser().getId()));
        return modelMapper.map(userCart, CartResponse.class);
    }

    @Override
    @Transactional
    public CartResponse updateCartItemQuantity(Long productId, Integer quantity, String action) {
        Long userId = authUtil.getCurrentUser().getId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
        CartItem cartItem = cartItemRepository
                .findByProductIdAndCartUserId(productId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId and userId",
                        productId + " and " + userId));
        int newQuantity = calculateNewQuantity(cartItem.getQuantity(), quantity, action);
        validateStockAvailability(product, newQuantity, action);
        if (newQuantity <= 0) {
            removeCartItem(cart, cartItem);
        } else {
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        }
        eventPublisher.publishEvent(new CartItemChangedEvent(this, cart.getId()));
        return modelMapper.map(cart, CartResponse.class);
    }

    @Override
    @Transactional
    public void removeCartItem(Long productId) {
        Long userId = authUtil.getCurrentUser().getId();
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
        CartItem cartItem = cartItemRepository
                .findByProductIdAndCartUserId(productId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId and userId",
                        productId + " and " + userId));

        removeCartItem(cart, cartItem);
        eventPublisher.publishEvent(new CartItemChangedEvent(this, cart.getId()));
    }

    @Override
    @Transactional
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(
                cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));
        cart.getCartItems().forEach(cartItem -> {
            cartItem.getProduct().getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        });
        cart.getCartItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    private int calculateNewQuantity(Integer currentQuantity, Integer requestedQuantity, String action) {
        return switch (action) {
            case "increment" -> currentQuantity + requestedQuantity;
            case "decrement" -> currentQuantity - requestedQuantity;
            case "set" -> requestedQuantity;
            default -> throw new APIException("Invalid action: " + action);
        };
    }

    private void validateStockAvailability(Product product, int newQuantity, String action) throws APIException {
        if (product.getStock() == 0) {
            throw new APIException("Product " + product.getName() + " is out of stock!");
        }

        if (("increment".equals(action) || "set".equals(action)) && newQuantity > product.getStock()) {
            throw new APIException("Insufficient stock for product: " + product.getName() + ". Available: "
                    + product.getStock() + ", Requested: " + newQuantity);
        }
    }

    private void removeCartItem(Cart cart, CartItem cartItem) {
        cart.getCartItems().remove(cartItem);
        cartItem.getProduct().getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
    }
}
