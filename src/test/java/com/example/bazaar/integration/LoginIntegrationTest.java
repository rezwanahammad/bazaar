package com.example.bazaar.integration;

import com.example.bazaar.model.User;
import com.example.bazaar.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testRegisterAndLoginIntegration() {
        String username = "integrationUser";
        String email = "integration@test.com";
        String rawPassword = "password";

        assertFalse(userRepository.findByUsername(username).isPresent());

        assertDoesNotThrow(() -> mockMvc.perform(post("/register")
                .with(csrf())
                .param("username", username)
                .param("email", email)
                .param("password", rawPassword)
                .param("role", "ROLE_BUYER"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login")));

        User found = userRepository.findByUsername(username).orElse(null);
        assertNotNull(found);
        assertEquals(email, found.getEmail());
        assertTrue(passwordEncoder.matches(rawPassword, found.getPassword()));

        assertDoesNotThrow(() -> mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin("/login")
                .user(username)
                .password(rawPassword))
            .andExpect(status().is3xxRedirection())
            .andExpect(authenticated().withUsername(username)));
    }
}