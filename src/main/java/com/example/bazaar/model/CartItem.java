package com.example.bazaar.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    private Long productId;
    private String productName;
    private String imageUrl;
    private BigDecimal unitPrice;
    private String size;
    private int quantity;

    public BigDecimal getLineTotal() {
        if (unitPrice == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
