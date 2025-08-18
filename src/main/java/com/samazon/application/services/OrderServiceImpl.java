package com.samazon.application.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.samazon.application.dto.orders.OrderRequest;
import com.samazon.application.dto.orders.OrderResponse;
import com.samazon.application.exceptions.APIException;
import com.samazon.application.models.Cart;
import com.samazon.application.models.CartItem;
import com.samazon.application.models.Order;
import com.samazon.application.models.OrderItem;
import com.samazon.application.models.Product;
import com.samazon.application.models.User;
import com.samazon.application.models.records.AddressRecord;
import com.samazon.application.models.enums.OrderStatus;
import com.samazon.application.repositories.CartRepository;
import com.samazon.application.repositories.OrderRepository;
import com.samazon.application.repositories.ProductRepository;
import com.samazon.application.utils.AuthUtil;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final CartService cartService;

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private final AuthUtil authUtil;

    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // Get current user and their cart
        User currentUser = authUtil.getCurrentUser();
        Cart userCart = cartRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new APIException("Cart not found for user: " + currentUser.getUsername()));
        List<CartItem> cartItems = userCart.getCartItems();

        if (cartItems.isEmpty()) {
            throw new APIException("Cannot create order with empty cart");
        }

        // Create order from request
        Order order = Order.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .orderStatus(OrderStatus.PENDING)
                .user(currentUser)
                .build();

        // Set shipping address
        order.setShippingAddress(new AddressRecord(request.getShippingAddress()));

        // Set billing address (use shipping if billing is not provided)
        if (request.getBillingAddress() != null) {
            order.setBillingAddress(new AddressRecord(request.getBillingAddress()));
        } else {
            order.setBillingAddress(order.getShippingAddress());
        }

        // Create order items and calculate total amount
        double totalAmount = 0.0;
        for (CartItem cartItem : cartItems) {
            // Validate product availability
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new APIException("Insufficient stock for product: " + product.getName());
            }

            double itemPrice = Optional.ofNullable(product.getSpecialPrice()).orElse(product.getPrice());

            OrderItem orderItem = OrderItem.builder()
                    .quantity(cartItem.getQuantity())
                    .unitPriceAtOrder(itemPrice)
                    .discount(product.getDiscount())
                    .productId(product.getId())
                    .productName(product.getName())
                    .productImage(product.getImage())
                    .sellerId(product.getSeller().getId())
                    .sellerName(product.getSeller().getUsername())
                    .order(order)
                    .build();

            order.getOrderItems().add(orderItem);

            // Calculate item total (quantity * special price)
            double itemTotal = cartItem.getQuantity() * itemPrice;
            totalAmount += itemTotal;

            // Update product stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(BigDecimal.valueOf(totalAmount).setScale(2, RoundingMode.HALF_UP));

        // Save order
        Order createdOrder = orderRepository.save(order);

        // Add order to user's orders
        currentUser.getOrders().add(createdOrder);

        // Clear the user's cart after successful order creation
        cartService.clearCart(userCart.getId());

        // Convert to response
        OrderResponse response = modelMapper.map(createdOrder, OrderResponse.class);
        log.warn("Order created: {}", createdOrder);

        return response;
    }

}
