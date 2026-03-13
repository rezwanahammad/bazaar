package com.example.bazaar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bazaar.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    long countByProductId(Long productId);

    @Query("select avg(r.rating) from Review r where r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);
}
