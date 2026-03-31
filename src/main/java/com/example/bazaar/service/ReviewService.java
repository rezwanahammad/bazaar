package com.example.bazaar.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.bazaar.dto.ReviewDto;
import com.example.bazaar.mapper.ReviewMapper;
import com.example.bazaar.model.Product;
import com.example.bazaar.model.Review;
import com.example.bazaar.repository.ProductRepository;
import com.example.bazaar.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ReviewMapper reviewMapper;

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

    public List<ReviewDto> getReviewDtosForProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    public ReviewDto createReview(Long productId, String reviewerName, Integer rating, String reviewText) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));

        Review review = new Review();
        review.setProduct(product);
        review.setReviewerName(reviewerName.trim());
        review.setRating(rating);
        review.setReviewText(reviewText.trim());
        return reviewMapper.toDto(reviewRepository.save(review));
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
