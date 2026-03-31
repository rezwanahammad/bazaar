package com.example.bazaar.mapper;

import org.springframework.stereotype.Component;

import com.example.bazaar.dto.ReviewDto;
import com.example.bazaar.model.Review;

@Component
public class ReviewMapper {

    public ReviewDto toDto(Review entity) {
        if (entity == null) {
            return null;
        }

        return ReviewDto.builder()
                .id(entity.getId())
                .productId(entity.getProduct() == null ? null : entity.getProduct().getId())
                .reviewerName(entity.getReviewerName())
                .rating(entity.getRating())
                .reviewText(entity.getReviewText())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
