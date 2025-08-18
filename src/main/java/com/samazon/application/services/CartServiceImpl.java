package com.samazon.application.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.samazon.application.dto.carts.CartItemResponse;
import com.samazon.application.dto.carts.CartResponse;
import com.samazon.application.exceptions.APIException;
import com.samazon.application.exceptions.ResourceNotFoundException;
import com.samazon.application.models.Cart;
import com.samazon.application.models.CartItem;
import com.samazon.application.models.Product;
import com.samazon.application.models.User;
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

    private final ModelMapper modelMapper;

    @Override
    public CartResponse addProductToCart(Long productId, Integer quantity) {
        // Find existing cart or create a new one
        Cart cart = _getCurrentUserCart();

        // Retrieve Product Details
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Perform validations
        if (cartItemRepository.findByCartIdAndProductId(cart.getId(), productId).isPresent()) {
            throw new APIException("Product " + product.getName() + " is already in the cart!");
        }

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

        updateProductStock(product);
        Cart updatedCart = updateCartTotal(cart);

        return mapCartToDTO(updatedCart);
    }

    @Override
    public List<CartResponse> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        return carts.stream()
                .map(this::mapCartToDTO)
                .toList();
    }

    @Override
    public CartResponse getCurrentUserCart() {
        Cart userCart = _getCurrentUserCart();
        return mapCartToDTO(userCart);
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
        updateProductStock(product);
        Cart updatedCart = updateCartTotal(cart);
        return mapCartToDTO(updatedCart);
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
        updateProductStock(cartItem.getProduct());
        updateCartTotal(cart);
    }

    @Override
    public List<Long> getAllCartIdsWithProduct(Long productId) {
        return cartRepository.findByCartItemsProductId(productId)
                .stream().map(Cart::getId).toList();
    }

    @Override
    public List<Long> getAllCartIdsWithCategory(Long categoryId) {
        return cartRepository.findByCartItemsProductCategoryId(categoryId)
                .stream().map(Cart::getId).toList();
    }

    @Override
    public void recalculateCartTotal(Long cartId) {
        cartRepository.findById(cartId).ifPresent(cart -> {
            cart.getCartItems().forEach(cartItem -> {
                if (cartItem.getProduct() != null) {
                    cartItem.setProduct(productRepository.findById(cartItem.getProduct().getId()).orElse(null));
                }
            });
            updateCartTotal(cart);
        });
    }

    @Override
    @Transactional
    public void clearCart(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", id));
        cart.getCartItems().forEach(cartItem -> {
            cartItem.getProduct().getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        });
        cart.getCartItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void createCartForUser(User newUser) {
        Cart cart = new Cart();
        cart.setUser(newUser);
        cart.setTotalAmount(BigDecimal.ZERO);
        newUser.setCart(cart);
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

    private Product updateProductStock(Product product) {
        product.setStock(product.getStock());
        return productRepository.save(product);
    }

    private Cart updateCartTotal(Cart cart) {
        cart.getCartItems().removeIf(ci -> ci.getProduct() == null);

        double totalAmount = cart.getCartItems().stream()
                .filter(item -> item.getProduct() != null) // safety
                .mapToDouble(item -> {
                    Product p = item.getProduct();
                    double unitPrice = p.getSpecialPrice() != null ? p.getSpecialPrice() : p.getPrice();
                    return unitPrice * item.getQuantity();
                })
                .sum();
        cart.setTotalAmount(BigDecimal.valueOf(totalAmount).setScale(2, RoundingMode.HALF_UP));
        return cartRepository.save(cart);
    }

    private Cart _getCurrentUserCart() {
        Cart userCart = cartRepository.findByUserId(authUtil.getCurrentUser().getId())
                .orElseThrow(() -> new APIException("Cart not found for user: " + authUtil.getCurrentUser().getId()));
        return userCart;
    }

    private CartResponse mapCartToDTO(Cart cart) {
        CartResponse response = modelMapper.map(cart, CartResponse.class);
        response.setCartItems(cart.getCartItems().stream()
                .map(cartItem -> modelMapper.map(cartItem, CartItemResponse.class))
                .toList());
        return response;
    }
}
