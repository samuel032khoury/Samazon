package com.samazon.application.events;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.samazon.application.models.Cart;
import com.samazon.application.models.Product;
import com.samazon.application.repositories.CartRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartItemChangedEventListener {

    private final CartRepository cartRepository;

    @EventListener
    @Transactional
    public void handleCartItemChanged(CartItemChangedEvent event) {
        cartRepository.findById(event.getCartId()).ifPresent(this::updateCartTotal);
    }

    @EventListener
    @Transactional
    public void handleProductChanged(ProductChangedEvent event) {
        List<Cart> carts = cartRepository.findByCartItemsProductId(event.getProductId());
        carts.forEach(this::updateCartTotal);
    }

    @EventListener
    @Transactional
    public void handleCategoryDeleted(CategoryDeletedEvent event) {
        List<Cart> carts = cartRepository.findAllById(event.getCartIds());
        carts.forEach(this::updateCartTotal);
    }

    @EventListener
    @Transactional
    public void handleProductDeleted(ProductDeletedEvent event) {
        List<Cart> carts = cartRepository.findAllById(event.getCartIds());
        carts.forEach(this::updateCartTotal);
    }

    private void updateCartTotal(Cart cart) {
        cart.getCartItems().removeIf(ci -> ci.getProduct() == null);

        double totalAmount = cart.getCartItems().stream()
                .filter(item -> item.getProduct() != null)
                .mapToDouble(item -> {
                    Product p = item.getProduct();
                    double unitPrice = p.getSpecialPrice() != null ? p.getSpecialPrice() : p.getPrice();
                    return unitPrice * item.getQuantity();
                })
                .sum();

        cart.setTotalAmount(BigDecimal.valueOf(totalAmount).setScale(2, RoundingMode.HALF_UP));
        cartRepository.save(cart);
    }
}
