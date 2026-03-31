package com.example.bazaar.service;

import com.example.bazaar.model.User;
import com.example.bazaar.mapper.UserMapper;
import com.example.bazaar.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Test
    void testRegisterUser() {
        UserRepository repo = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        UserService service = new UserService(repo, encoder, new UserMapper());

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPassword("123");

        when(encoder.encode("123")).thenReturn("encoded123");
        when(repo.save(Mockito.any(User.class))).thenReturn(user);

        User saved = service.registerUser(user);

        assertNotNull(saved);
        assertEquals("testuser", saved.getUsername());
        assertEquals("encoded123", user.getPassword());

        verify(repo, times(1)).save(user);
        verify(encoder, times(1)).encode("123");
    }
}