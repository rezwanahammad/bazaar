package com.example.bazaar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequest {
    private Long productId;
    private String size;
    private Integer quantity;
}
