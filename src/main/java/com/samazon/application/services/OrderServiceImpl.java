package com.samazon.application.services;

import java.time.LocalDate;
import java.util.ArrayList;
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
import com.samazon.application.models.Payment;
import com.samazon.application.models.Product;
import com.samazon.application.models.User;
import com.samazon.application.repositories.AddressRepository;
import com.samazon.application.repositories.CartRepository;
import com.samazon.application.repositories.OrderItemRepository;
import com.samazon.application.repositories.OrderRepository;
import com.samazon.application.repositories.PaymentRepository;
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
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
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
        // Create a new Order with payment info
        Order order = new Order();
        order.setUser(currentUser);
        order.setOrderDate(LocalDate.now());
        order.setAddress(address);
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("ACCEPTED");

        Payment payment = Payment.builder()
                .paymentMethod(paymentMethod)
                .paymentGatewayProvider(paymentGatewayProvider)
                .paymentGatewayId(paymentGatewayId)
                .paymentGatewayStatus(paymentGatewayStatus)
                .paymentGatewayResponse(paymentGatewayResponse)
                .order(order)
                .build();
        Payment orderPayment = paymentRepository.save(payment);
        order.setPayment(orderPayment);
        Order newOrder = orderRepository.save(order);
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new APIException("No items found in the cart to place an order");
        }
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(newOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setPriceAtOrder(productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", cartItem.getProduct().getId()))
                    .getPrice());
            orderItems.add(orderItem);
        }
        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);
        newOrder.setOrderItems(savedOrderItems);

        // Update product stock
        cart.getCartItems().forEach(cartItem -> {
            int quantity = cartItem.getQuantity();
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
        });

        cartService.clearCart(cart.getId());

        return OrderDTO.fromEntity(newOrder, modelMapper);
    }

}
