package com.example.bazaar.integration;

import com.example.bazaar.enums.PaymentMethod;
import com.example.bazaar.model.CartItemEntity;
import com.example.bazaar.model.OrderEntity;
import com.example.bazaar.model.User;
import com.example.bazaar.repository.UserRepository;
import com.example.bazaar.service.CartService;
import com.example.bazaar.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private CartService cartService;

    @Test
    void testPlaceOrderIntegration() {
        User user = new User();
        user.setUsername("integrationUser");
        user.setEmail("test@test.com");
        user.setPassword("encoded");
        user.setAddress("Dhaka");

        userRepository.save(user);

        CartItemEntity item = new CartItemEntity();
        item.setProductId(1L);
        item.setProductName("Test Product");
        item.setImageUrl("img.jpg");
        item.setSize("M");
        item.setUnitPrice(BigDecimal.valueOf(100));
        item.setQuantity(2);

        when(cartService.getCartForUser("integrationUser"))
                .thenReturn(List.of(item));

        OrderEntity order = orderService.placeOrder(
                "integrationUser",
                PaymentMethod.COD,
                null,
                null);

        assertNotNull(order.getId());
        assertEquals(BigDecimal.valueOf(200), order.getTotalAmount());

        verify(cartService).clearCart("integrationUser");
    }
}