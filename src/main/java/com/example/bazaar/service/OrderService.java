package com.example.bazaar.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bazaar.dto.OrderDto;
import com.example.bazaar.dto.ProductDto;
import com.example.bazaar.enums.OrderStatus;
import com.example.bazaar.enums.PaymentMethod;
import com.example.bazaar.enums.PaymentStatus;
import com.example.bazaar.exception.ResourceNotFoundException;
import com.example.bazaar.mapper.OrderMapper;
import com.example.bazaar.model.OrderEntity;
import com.example.bazaar.model.OrderItem;
import com.example.bazaar.model.Payment;
import com.example.bazaar.model.Product;
import com.example.bazaar.model.User;
import com.example.bazaar.repository.OrderRepository;
import com.example.bazaar.repository.ProductRepository;
import com.example.bazaar.repository.UserRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, CartService cartService, OrderMapper orderMapper) {
        this(orderRepository, userRepository, cartService, orderMapper, null);
    }

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            CartService cartService,
            OrderMapper orderMapper,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.orderMapper = orderMapper;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderEntity placeOrder(String username, PaymentMethod method, String transactionId, String phone) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (method == null) {
            throw new IllegalArgumentException("Please select a payment method.");
        }

        List<Product> cartItems = cartService.getCartForUser(username);

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
            .map(product -> product.getPrice() == null ? BigDecimal.ZERO : product.getPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(method);
        order.setShippingAddress(user.getAddress());
        order.setNote("Order placed by " + username);

        for (Product cartProduct : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            attachProductReference(item, cartProduct.getId());
            item.setProductName(cartProduct.getName());
            item.setImageUrl(cartProduct.getImageUrl());
            item.setSize("N/A");
            item.setUnitPrice(cartProduct.getPrice());
            item.setQuantity(1);
            item.setLineTotal(cartProduct.getPrice() == null ? BigDecimal.ZERO : cartProduct.getPrice());
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

    public List<OrderDto> getOrderDtosForUser(String username) {
        return orderRepository.findByUserUsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    public OrderEntity getOrderForUser(Long orderId, String username) {
        return orderRepository.findByIdAndUserUsername(orderId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found."));
    }

    public OrderDto getOrderDtoForUser(Long orderId, String username) {
        OrderEntity order = orderRepository.findByIdAndUserUsername(orderId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found."));
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDto placeOrderDto(String username, PaymentMethod method, String transactionId, String phone) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (method == null) {
            throw new IllegalArgumentException("Please select a payment method.");
        }

        List<ProductDto> cartItems = cartService.getCartDtosForUser(username);

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
            .map(product -> product.getPrice() == null ? BigDecimal.ZERO : product.getPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(method);
        order.setShippingAddress(user.getAddress());
        order.setNote("Order placed by " + username);

        for (ProductDto cartItem : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            attachProductReference(item, cartItem.getId());
            item.setProductName(cartItem.getName());
            item.setImageUrl(cartItem.getImageUrl());
            item.setSize("N/A");
            item.setUnitPrice(cartItem.getPrice());
            item.setQuantity(1);
            item.setLineTotal(cartItem.getPrice() == null ? BigDecimal.ZERO : cartItem.getPrice());
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

        return orderMapper.toDto(saved);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void attachProductReference(OrderItem item, Long productId) {
        if (productId == null) {
            throw new ResourceNotFoundException("Product not found.");
        }

        if (productRepository != null) {
            item.setProduct(productRepository.getReferenceById(productId));
            return;
        }

        item.setProductId(productId);
    }
}
