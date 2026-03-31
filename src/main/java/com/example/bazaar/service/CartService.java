package com.example.bazaar.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bazaar.dto.CartItemDto;
import com.example.bazaar.mapper.CartItemMapper;
import com.example.bazaar.model.CartItemEntity;
import com.example.bazaar.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemMapper cartItemMapper;

    public List<CartItemEntity> getCartForUser(String username) {
        return cartRepository.findByUsernameOrderByIdAsc(username);
    }

    public List<CartItemDto> getCartDtosForUser(String username) {
        return cartRepository.findByUsernameOrderByIdAsc(username)
                .stream()
                .map(cartItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addItem(String username, Long productId, String productName,
                        String imageUrl, BigDecimal price, String size, int quantity) {
        cartRepository.findByUsernameAndProductIdAndSize(username, productId, size)
                .ifPresentOrElse(
                        existing -> {
                            existing.setQuantity(existing.getQuantity() + quantity);
                            cartRepository.save(existing);
                        },
                        () -> cartRepository.save(
                                new CartItemEntity(null, username, productId, productName, imageUrl, price, size, quantity)
                        )
                );
    }

    @Transactional
    public void removeItem(String username, Long productId, String size) {
        cartRepository.deleteByUsernameAndProductIdAndSize(username, productId, size);
    }

    @Transactional
    public void clearCart(String username) {
        cartRepository.deleteByUsername(username);
    }
}
