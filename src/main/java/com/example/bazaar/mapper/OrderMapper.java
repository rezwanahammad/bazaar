package com.example.bazaar.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.bazaar.dto.OrderDto;
import com.example.bazaar.dto.OrderItemDto;
import com.example.bazaar.model.OrderEntity;
import com.example.bazaar.model.OrderItem;
import com.example.bazaar.repository.ProductRepository;

@Component
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;
    private final ProductRepository productRepository;

    public OrderMapper(OrderItemMapper orderItemMapper) {
        this(orderItemMapper, null);
    }

    @Autowired
    public OrderMapper(OrderItemMapper orderItemMapper, ProductRepository productRepository) {
        this.orderItemMapper = orderItemMapper;
        this.productRepository = productRepository;
    }

    public OrderDto toDto(OrderEntity entity) {
        if (entity == null) {
            return null;
        }

        List<OrderItemDto> items = entity.getItems() == null
                ? List.of()
                : entity.getItems().stream().map(orderItemMapper::toDto).collect(Collectors.toList());

        return OrderDto.builder()
                .id(entity.getId())
                .userId(entity.getUser() == null ? null : entity.getUser().getId())
                .username(entity.getUser() == null ? null : entity.getUser().getUsername())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .paymentMethod(entity.getPaymentMethod())
                .shippingAddress(entity.getShippingAddress())
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .items(items)
                .build();
    }

    public OrderEntity toEntity(OrderDto dto) {
        if (dto == null) {
            return null;
        }

        OrderEntity entity = new OrderEntity();
        entity.setId(dto.getId());
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setStatus(dto.getStatus());
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setShippingAddress(dto.getShippingAddress());
        entity.setNote(dto.getNote());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        if (dto.getItems() != null) {
            for (OrderItemDto itemDto : dto.getItems()) {
                OrderItem item = new OrderItem();
                item.setId(itemDto.getId());
                item.setOrder(entity);
                if (itemDto.getProductId() != null) {
                    if (productRepository != null) {
                        item.setProduct(productRepository.getReferenceById(itemDto.getProductId()));
                    } else {
                        item.setProductId(itemDto.getProductId());
                    }
                }
                item.setProductName(itemDto.getProductName());
                item.setImageUrl(itemDto.getImageUrl());
                item.setSize(itemDto.getSize());
                item.setUnitPrice(itemDto.getUnitPrice());
                item.setQuantity(itemDto.getQuantity());
                item.setLineTotal(itemDto.getLineTotal());
                entity.getItems().add(item);
            }
        }

        return entity;
    }
}
