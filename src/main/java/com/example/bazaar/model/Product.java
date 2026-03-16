package com.example.bazaar.model;

import com.example.bazaar.enums.Category;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 2000)
    private String description;

    private BigDecimal price;

    private Integer stockQuantity;

    private String imageUrl;

    private String brand;

    @Enumerated(EnumType.STRING)
    private Category category;

    private Boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    private String sellerUsername;

}