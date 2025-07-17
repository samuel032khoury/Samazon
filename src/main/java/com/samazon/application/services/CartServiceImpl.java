package com.samazon.application.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.samazon.application.dto.CartDTO;
import com.samazon.application.exceptions.APIException;
import com.samazon.application.exceptions.ResourceNotFoundException;
import com.samazon.application.models.Cart;
import com.samazon.application.models.CartItem;
import com.samazon.application.models.Product;
import com.samazon.application.repositories.CartItemRepository;
import com.samazon.application.repositories.CartRepository;
import com.samazon.application.repositories.ProductRepository;
import com.samazon.application.utils.AuthUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final AuthUtil authUtil;

    private final ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        // Find existing cart or create a new one
        Cart cart = getCurrentUserCart();

        // Retrieve Product Details
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Perform validations
        if (cartItemRepository.findByCartIdAndProductId(cart.getId(), productId).isPresent()) {
            throw new APIException("Product " + product.getName() + " is already in the cart!");
        }

        if (product.getStock() == 0) {
            throw new APIException("Product " + product.getName() + " is out of stock!");
        }

        if (product.getStock() < quantity) {
            throw new APIException("Product " + product.getName() + " does not have enough stock! Available: "
                    + product.getStock() + ", Requested: " + quantity);
        }

        // Create CartItem and save it
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setDiscount(product.getDiscount());
        cartItem.setPrice(product.getSpecialPrice() != null ? product.getSpecialPrice() : product.getPrice());
        CartItem savedCartItem = cartItemRepository.save(cartItem);

        product.setStock(product.getStock());
        productRepository.save(product);

        cart.setTotalPrice(cart.getTotalPrice() + (savedCartItem.getPrice() * quantity));
        cart.getCartItems().add(savedCartItem);
        cartRepository.save(cart);

        return modelMapper.map(cart, CartDTO.class);
    }

    private Cart getCurrentUserCart() {
        Cart userCart = cartRepository.findByUserId(authUtil.getCurrentUser().getId());
        if (userCart == null) {
            userCart = new Cart();
            userCart.setUser(authUtil.getCurrentUser());
            cartRepository.save(userCart);
        }
        return userCart;
    }

}
