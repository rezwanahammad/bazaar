package com.example.bazaar.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bazaar.enums.OrderStatus;
import com.example.bazaar.enums.PaymentMethod;
import com.example.bazaar.enums.PaymentStatus;
import com.example.bazaar.model.CartItemEntity;
import com.example.bazaar.model.OrderEntity;
import com.example.bazaar.model.OrderItem;
import com.example.bazaar.model.Payment;
import com.example.bazaar.model.User;
import com.example.bazaar.repository.OrderRepository;
import com.example.bazaar.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @Transactional
    public OrderEntity placeOrder(String username, PaymentMethod method, String transactionId, String phone) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (method == null) {
            throw new IllegalArgumentException("Please select a payment method.");
        }

        List<CartItemEntity> cartItems = cartService.getCartForUser(username);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Your cart is empty.");
        }

        if (method.isManualGateway()) {
            if (transactionId == null || transactionId.isBlank()) {
                throw new IllegalArgumentException("Transaction ID is required for bKash/Nagad.");
            }
            if (phone == null || phone.isBlank()) {
                throw new IllegalArgumentException("Phone number is required for bKash/Nagad.");
            }
        }

        BigDecimal total = cartItems.stream()
                .map(CartItemEntity::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(method);
        order.setShippingAddress(user.getAddress());
        order.setNote("Order placed by " + username);

        for (CartItemEntity cartItem : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(cartItem.getProductId());
            item.setProductName(cartItem.getProductName());
            item.setImageUrl(cartItem.getImageUrl());
            item.setSize(cartItem.getSize());
            item.setUnitPrice(cartItem.getUnitPrice());
            item.setQuantity(cartItem.getQuantity());
            item.setLineTotal(cartItem.getLineTotal());
            order.getItems().add(item);
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(method);
        payment.setTransactionId(isBlank(transactionId) ? null : transactionId.trim());
        payment.setPhone(isBlank(phone) ? null : phone.trim());
        payment.setStatus(method == PaymentMethod.COD
                ? PaymentStatus.NOT_REQUIRED
                : PaymentStatus.PENDING);
        payment.setPaidAt(method == PaymentMethod.COD ? null : LocalDateTime.now());

        order.setPayment(payment);

        OrderEntity saved = orderRepository.save(order);
        cartService.clearCart(username);

        return saved;
    }

    public List<OrderEntity> getOrdersForUser(String username) {
        return orderRepository.findByUserUsernameOrderByCreatedAtDesc(username);
    }

    public OrderEntity getOrderForUser(Long orderId, String username) {
        return orderRepository.findByIdAndUserUsername(orderId, username)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
