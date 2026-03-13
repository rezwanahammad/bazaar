package com.example.bazaar.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.bazaar.model.Product;
import com.example.bazaar.model.Review;
import com.example.bazaar.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Review createReview(Product product, String reviewerName, Integer rating, String reviewText) {
        Review review = new Review();
        review.setProduct(product);
        review.setReviewerName(reviewerName.trim());
        review.setRating(rating);
        review.setReviewText(reviewText.trim());
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsForProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public long getReviewCountForProduct(Long productId) {
        return reviewRepository.countByProductId(productId);
    }

    public BigDecimal getAverageRatingForProduct(Long productId) {
        Double avg = reviewRepository.findAverageRatingByProductId(productId);
        if (avg == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP);
    }
}
