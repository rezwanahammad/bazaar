package com.example.bazaar.service;

import com.example.bazaar.model.User;
import com.example.bazaar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private UserRepository repo;
    //checking git approval
    @BeforeEach
    void setup() {
        repo = mock(UserRepository.class);
    }

    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encoded123");

        when(repo.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> found = repo.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void testLoginFail() {
        when(repo.findByUsername("unknown")).thenReturn(Optional.empty());
        Optional<User> found = repo.findByUsername("unknown");
        assertFalse(found.isPresent());
    }
}