package com.example.bazaar.integration;

import com.example.bazaar.model.OrderEntity;
import com.example.bazaar.model.Product;
import com.example.bazaar.model.User;
import com.example.bazaar.repository.OrderRepository;
import com.example.bazaar.repository.ProductRepository;
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
    private com.example.bazaar.repository.OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @WithMockUser(username = "integrationUser", roles = {"BUYER"})
    void testPlaceOrderIntegration() {
        User user = new User();
        user.setUsername("integrationUser");
        user.setEmail("test@test.com");
        user.setPassword("encoded");
        user.setAddress("Dhaka");

        userRepository.save(user);

        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100));
        product.setActive(true);
        product = productRepository.save(product);

        user.getCartProducts().add(product);
        userRepository.save(user);

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
        assertEquals(BigDecimal.valueOf(100), order.getTotalAmount());
        assertEquals("integrationUser", order.getUser().getUsername());

        assertTrue(userRepository.findByUsername("integrationUser").orElseThrow().getCartProducts().isEmpty());
    }
}