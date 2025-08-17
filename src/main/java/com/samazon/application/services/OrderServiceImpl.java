package com.samazon.application.services;

import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.samazon.application.dto.orders.OrderRequest;
import com.samazon.application.dto.orders.OrderDTO;
import com.samazon.application.exceptions.APIException;
import com.samazon.application.exceptions.ResourceNotFoundException;
import com.samazon.application.models.Address;
import com.samazon.application.models.Cart;
import com.samazon.application.models.CartItem;
import com.samazon.application.models.Order;
import com.samazon.application.models.OrderItem;
import com.samazon.application.models.OrderStatus;
import com.samazon.application.models.Payment;
import com.samazon.application.models.Product;
import com.samazon.application.models.User;
import com.samazon.application.repositories.AddressRepository;
import com.samazon.application.repositories.CartRepository;
import com.samazon.application.repositories.OrderRepository;
import com.samazon.application.repositories.ProductRepository;
import com.samazon.application.utils.AuthUtil;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartService cartService;

    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private final AuthUtil authUtil;

    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(OrderRequest request) {
        User currentUser = authUtil.getCurrentUser();
        // Extract request details
        Long addressId = request.getAddressId();
        String paymentMethod = request.getPaymentMethod();
        String paymentGatewayProvider = request.getPaymentGatewayProvider();
        String paymentGatewayId = request.getPaymentGatewayId();
        String paymentGatewayStatus = request.getPaymentGatewayStatus();
        String paymentGatewayResponse = request.getPaymentGatewayResponse();
        // Getting User Cart
        Long useCartId = cartService.getCurrentUserCart().getId();
        Cart cart = cartRepository.findById(useCartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", useCartId));
        if (cart.getCartItems().isEmpty()) {
            throw new APIException("Cart is empty, cannot place order");
        }
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        // Create a new Order initially without payment
        Order order = new Order();
        order.setUser(currentUser);
        order.setOrderDate(LocalDate.now());
        order.setAddress(address);
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus(OrderStatus.PENDING); // Start with PENDING until payment is successful

        // Create OrderItems directly and add to order
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new APIException("No items found in the cart to place an order");
        }

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .discount(cartItem.getDiscount())
                    .priceAtOrder(cartItem.getProduct().getPrice())
                    .build();
            order.getOrderItems().add(orderItem);
        }

        // Process payment for the order
        Payment payment = Payment.builder()
                .paymentMethod(paymentMethod)
                .paymentGatewayProvider(paymentGatewayProvider)
                .paymentGatewayId(paymentGatewayId)
                .paymentGatewayStatus(paymentGatewayStatus)
                .paymentGatewayResponse(paymentGatewayResponse)
                .build();

        // Establish bidirectional relationship
        order.setPayment(payment);
        payment.setOrder(order);

        // Update order status based on payment status
        if ("SUCCESS".equalsIgnoreCase(paymentGatewayStatus) || "COMPLETED".equalsIgnoreCase(paymentGatewayStatus)) {
            order.setOrderStatus(OrderStatus.CONFIRMED);
        } else if ("PENDING".equalsIgnoreCase(paymentGatewayStatus)) {
            order.setOrderStatus(OrderStatus.PENDING);
        } else {
            order.setOrderStatus(OrderStatus.CANCELLED);
        } // Single save operation - cascades will handle OrderItems and Payment
        Order savedOrder = orderRepository.save(order);

        // Only update product stock and clear cart if payment was successful
        if (OrderStatus.CONFIRMED.equals(savedOrder.getOrderStatus())) {
            // Update product stock
            cart.getCartItems().forEach(cartItem -> {
                int quantity = cartItem.getQuantity();
                Product product = cartItem.getProduct();
                product.setStock(product.getStock() - quantity);
                productRepository.save(product);
            });

            // Clear cart only after successful payment
            cartService.clearCart(cart.getId());
        }

        return OrderDTO.fromEntity(savedOrder, modelMapper);
    }

    /**
     * Updates payment status and corresponding order status
     * This method handles payment gateway webhooks or status updates
     */
    @Transactional
    public OrderDTO updatePaymentStatus(Long orderId, String paymentGatewayStatus, String paymentGatewayResponse) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (order.getPayment() == null) {
            throw new APIException("No payment associated with this order");
        }

        Payment payment = order.getPayment();
        payment.setPaymentGatewayStatus(paymentGatewayStatus);
        payment.setPaymentGatewayResponse(paymentGatewayResponse);

        // Update order status based on payment status
        OrderStatus oldStatus = order.getOrderStatus();
        if ("SUCCESS".equalsIgnoreCase(paymentGatewayStatus) || "COMPLETED".equalsIgnoreCase(paymentGatewayStatus)) {
            order.setOrderStatus(OrderStatus.CONFIRMED);

            // If this is the first successful payment, update stock
            if (!OrderStatus.CONFIRMED.equals(oldStatus)) {
                // Note: In real implementation, you'd want to get CartItems from somewhere
                // or store them in the Order for this scenario
                order.getOrderItems().forEach(orderItem -> {
                    Product product = orderItem.getProduct();
                    product.setStock(product.getStock() - orderItem.getQuantity());
                    productRepository.save(product);
                });
            }
        } else if ("PENDING".equalsIgnoreCase(paymentGatewayStatus)) {
            order.setOrderStatus(OrderStatus.PENDING);
        } else {
            order.setOrderStatus(OrderStatus.CANCELLED);
        }

        Order updatedOrder = orderRepository.save(order);
        return OrderDTO.fromEntity(updatedOrder, modelMapper);
    }

}
