package com.example.bazaar.integration;

import com.example.bazaar.model.CartItemEntity;
import com.example.bazaar.model.OrderEntity;
import com.example.bazaar.model.User;
import com.example.bazaar.repository.CartRepository;
import com.example.bazaar.repository.OrderRepository;
import com.example.bazaar.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @WithMockUser(username = "integrationUser", roles = {"BUYER"})
    void testPlaceOrderIntegration() {
        User user = new User();
        user.setUsername("integrationUser");
        user.setEmail("test@test.com");
        user.setPassword("encoded");
        user.setAddress("Dhaka");

        userRepository.save(user);

        CartItemEntity item = new CartItemEntity();
    item.setUsername("integrationUser");
        item.setProductId(1L);
        item.setProductName("Test Product");
        item.setImageUrl("img.jpg");
        item.setSize("M");
        item.setUnitPrice(BigDecimal.valueOf(100));
        item.setQuantity(2);
    cartRepository.save(item);

    MvcResult result = assertDoesNotThrow(() -> mockMvc.perform(post("/checkout/place")
            .with(csrf())
            .param("paymentMethod", "COD"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("/orders/*/success"))
        .andReturn());

    String redirectUrl = result.getResponse().getRedirectedUrl();
    assertNotNull(redirectUrl);

    Matcher matcher = Pattern.compile("/orders/(\\d+)/success").matcher(redirectUrl);
    assertTrue(matcher.matches());
    Long orderId = Long.parseLong(matcher.group(1));

    Optional<OrderEntity> savedOrder = orderRepository.findById(orderId);
    assertTrue(savedOrder.isPresent());

    OrderEntity order = savedOrder.get();
    assertEquals(BigDecimal.valueOf(200), order.getTotalAmount());
    assertEquals("integrationUser", order.getUser().getUsername());

    assertTrue(cartRepository.findByUsernameOrderByIdAsc("integrationUser").isEmpty());
    }
}