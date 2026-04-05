package com.example.bazaar.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bazaar.dto.ProductDto;
import com.example.bazaar.exception.ResourceNotFoundException;
import com.example.bazaar.mapper.ProductMapper;
import com.example.bazaar.model.Product;
import com.example.bazaar.model.User;
import com.example.bazaar.repository.ProductRepository;
import com.example.bazaar.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<Product> getCartForUser(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        return List.copyOf(user.getCartProducts());
    }

    public List<ProductDto> getCartDtosForUser(String username) {
        return getCartForUser(username)
                .stream()
            .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addItem(String username, Long productId, String productName,
            String imageUrl, BigDecimal price, String size, int quantity) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found."));

        boolean alreadyInCart = user.getCartProducts().stream()
            .anyMatch(existing -> productId.equals(existing.getId()));

        if (!alreadyInCart) {
            user.getCartProducts().add(product);
            userRepository.save(user);
        }
    }

    @Transactional
    public void removeItem(String username, Long productId, String size) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        user.getCartProducts().removeIf(product -> productId.equals(product.getId()));
        userRepository.save(user);
    }

    @Transactional
    public void clearCart(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        user.getCartProducts().clear();
        userRepository.save(user);
    }
}
