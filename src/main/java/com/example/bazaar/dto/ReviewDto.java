package com.example.bazaar.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    private Long id;
    private Long productId;
    private String reviewerName;
    private Integer rating;
    private String reviewText;
    private LocalDateTime createdAt;
}
