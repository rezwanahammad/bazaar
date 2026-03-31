package com.example.bazaar.mapper;

import org.springframework.stereotype.Component;

import com.example.bazaar.dto.CartItemDto;
import com.example.bazaar.model.CartItemEntity;

@Component
public class CartItemMapper {

    public CartItemDto toDto(CartItemEntity entity) {
        if (entity == null) {
            return null;
        }

        return CartItemDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .imageUrl(entity.getImageUrl())
                .unitPrice(entity.getUnitPrice())
                .size(entity.getSize())
                .quantity(entity.getQuantity())
                .build();
    }

    public CartItemEntity toEntity(CartItemDto dto) {
        if (dto == null) {
            return null;
        }

        CartItemEntity entity = new CartItemEntity();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setProductId(dto.getProductId());
        entity.setProductName(dto.getProductName());
        entity.setImageUrl(dto.getImageUrl());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setSize(dto.getSize());
        entity.setQuantity(dto.getQuantity());
        return entity;
    }
}
