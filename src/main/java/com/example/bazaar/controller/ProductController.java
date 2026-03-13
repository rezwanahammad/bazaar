package com.example.bazaar.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bazaar.enums.Category;
import com.example.bazaar.model.Product;
import com.example.bazaar.model.Review;
import com.example.bazaar.service.ProductService;
import com.example.bazaar.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;

    @GetMapping("/products")
    public String products(@RequestParam(value = "category", required = false) String category, Model model) {
        List<Product> products;
        String selectedCategory = "All Products";

        if (category != null && !category.isBlank()) {
            Category parsedCategory = parseCategory(category);
            products = productService.getActiveProductsByCategory(parsedCategory);
            selectedCategory = parsedCategory.name().replace('_', ' ');
        } else {
            products = productService.getActiveProducts();
        }

        model.addAttribute("products", products);
        model.addAttribute("selectedCategory", selectedCategory);
        return "products/list";
    }

    @GetMapping("/products/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        List<Product> relatedProducts = productService.getRelatedProducts(product);
        List<Review> reviews = reviewService.getReviewsForProduct(id);
        BigDecimal oldPrice = product.getPrice() != null
                ? product.getPrice().add(BigDecimal.valueOf(500))
                : BigDecimal.ZERO;
        BigDecimal averageRating = reviewService.getAverageRatingForProduct(id);
        long reviewCount = reviewService.getReviewCountForProduct(id);

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("oldPrice", oldPrice);
        return "products/detail";
    }

    @PostMapping("/products/{id}/reviews")
    public String submitReview(
            @PathVariable Long id,
            @RequestParam("reviewerName") String reviewerName,
            @RequestParam("rating") Integer rating,
            @RequestParam("reviewText") String reviewText,
            RedirectAttributes redirectAttributes
    ) {
        if (reviewerName == null || reviewerName.isBlank() || reviewText == null || reviewText.isBlank()) {
            redirectAttributes.addFlashAttribute("reviewError", "Please fill in your name and review before submitting.");
            return "redirect:/products/" + id;
        }

        if (rating < 1 || rating > 5) {
            redirectAttributes.addFlashAttribute("reviewError", "Rating must be between 1 and 5 stars.");
            return "redirect:/products/" + id;
        }

        Product product = productService.getProductById(id);
        reviewService.createReview(product, reviewerName, rating, reviewText);
        redirectAttributes.addFlashAttribute("reviewSuccess", "Thanks! Your review has been submitted successfully.");
        return "redirect:/products/" + id;
    }

    private Category parseCategory(String rawCategory) {
        String normalized = rawCategory.trim().replace('-', '_').toUpperCase();
        try {
            return Category.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid category: " + rawCategory);
        }
    }
}
