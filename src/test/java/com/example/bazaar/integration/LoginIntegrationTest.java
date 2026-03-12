package com.example.bazaar.integration;

import com.example.bazaar.model.User;
import com.example.bazaar.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // rollback after test
public class LoginIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testRegisterAndLoginIntegration() {
        User user = new User();
        user.setUsername("integrationUser");
        user.setEmail("integration@test.com");
        user.setPassword(passwordEncoder.encode("password"));

        userRepository.save(user);

        User found = userRepository.findByUsername("integrationUser").orElse(null);
        assertNotNull(found);
        assertTrue(passwordEncoder.matches("password", found.getPassword()));
    }
}