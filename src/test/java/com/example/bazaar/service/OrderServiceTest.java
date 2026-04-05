package com.example.bazaar.service;

import com.example.bazaar.enums.PaymentMethod;
import com.example.bazaar.mapper.OrderItemMapper;
import com.example.bazaar.mapper.OrderMapper;
import com.example.bazaar.model.OrderEntity;
import com.example.bazaar.model.Product;
import com.example.bazaar.model.User;
import com.example.bazaar.repository.OrderRepository;
import com.example.bazaar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

        private com.example.bazaar.repository.OrderRepository orderRepository;
        private com.example.bazaar.repository.UserRepository userRepository;
    private CartService cartService;
        private com.example.bazaar.mapper.OrderMapper orderMapper;
    private OrderService orderService;

    @BeforeEach
    void setup() {
                orderRepository = mock(com.example.bazaar.repository.OrderRepository.class);
                userRepository = mock(com.example.bazaar.repository.UserRepository.class);
        cartService = mock(CartService.class);
                orderMapper = new com.example.bazaar.mapper.OrderMapper(new OrderItemMapper());

                orderService = new OrderService(orderRepository, userRepository, cartService, orderMapper);
    }

    @Test
    void testPlaceOrderSuccess() {

        String username = "user1";

        User user = new User();
        user.setUsername(username);
        user.setAddress("Dhaka");

        Product product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setImageUrl("img.jpg");
        product.setPrice(BigDecimal.valueOf(100));

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(cartService.getCartForUser(username))
                .thenReturn(List.of(product));

        when(orderRepository.save(any(OrderEntity.class)))
                .thenAnswer(i -> i.getArgument(0));

        OrderEntity result = orderService.placeOrder(
                username,
                PaymentMethod.COD,
                null,
                null
        );

        assertNotNull(result);
        verify(orderRepository).save(any(OrderEntity.class));
        verify(cartService).clearCart(username);
    }

    @Test
    void testPlaceOrderEmptyCart() {

        String username = "user";

        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(cartService.getCartForUser(username))
                .thenReturn(List.of());

        assertThrows(IllegalStateException.class,
                () -> orderService.placeOrder(username, PaymentMethod.COD, null, null));
    }

    @Test
    void testPlaceOrderUserNotFound() {

        String username = "user";

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        when(cartService.getCartForUser(username))
                .thenReturn(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(username, PaymentMethod.COD, null, null));
    }

    @Test
    void testGetOrdersForUser() {

        when(orderRepository.findByUserUsernameOrderByCreatedAtDesc("user"))
                .thenReturn(List.of(new OrderEntity()));

        List<OrderEntity> result = orderService.getOrdersForUser("user");

        assertEquals(1, result.size());
    }

    @Test
    void testGetOrderForUserNotFound() {

        when(orderRepository.findByIdAndUserUsername(1L, "user"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> orderService.getOrderForUser(1L, "user"));
    }
}