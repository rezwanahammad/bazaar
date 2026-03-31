package com.example.bazaar.mapper;

import org.springframework.stereotype.Component;

import com.example.bazaar.dto.OrderItemDto;
import com.example.bazaar.model.OrderItem;

@Component
public class OrderItemMapper {

    public OrderItemDto toDto(OrderItem entity) {
        if (entity == null) {
            return null;
        }

        return OrderItemDto.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .imageUrl(entity.getImageUrl())
                .size(entity.getSize())
                .unitPrice(entity.getUnitPrice())
                .quantity(entity.getQuantity())
                .lineTotal(entity.getLineTotal())
                .build();
    }
}
