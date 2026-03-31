package com.example.bazaar.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.bazaar.dto.UserDto;
import com.example.bazaar.mapper.UserMapper;
import com.example.bazaar.model.User;
import com.example.bazaar.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @PostConstruct
    public void seedAdmin() {
        boolean adminExists = userRepository.findAll()
                .stream()
                .anyMatch(u -> "ROLE_ADMIN".equals(u.getRole()));
        if (!adminExists) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@bazaar.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
        }
    }

    public User registerUser(User user){
        if (user.getEmail() != null) {
            user.setEmail(user.getEmail().trim().toLowerCase());
        }
        if (user.getFullName() != null) {
            user.setFullName(user.getFullName().trim());
        }
        if (user.getPhone() != null) {
            user.setPhone(user.getPhone().trim());
        }
        if (user.getAddress() != null) {
            user.setAddress(user.getAddress().trim());
        }
        if (user.getBillingAddress() != null) {
            user.setBillingAddress(user.getBillingAddress().trim());
        }

        // Registration no longer asks for username explicitly; use email as the login username.
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            user.setUsername(user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Whitelist: only BUYER or SELLER may be chosen at self-registration
        String requestedRole = user.getRole();
        if ("ROLE_SELLER".equals(requestedRole)) {
            user.setRole("ROLE_SELLER");
        } else {
            user.setRole("ROLE_BUYER");
        }
        return userRepository.save(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public List<UserDto> getAllUserDtos() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public void setRole(Long id, String role){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        userRepository.save(user);
    }
}
