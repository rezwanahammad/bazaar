package com.example.bazaar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.bazaar.enums.Category;
import com.example.bazaar.model.Product;
import com.example.bazaar.service.CloudinaryImageService;
import com.example.bazaar.service.ProductService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class AdminProductController {

    private final CloudinaryImageService cloudinaryImageService;
    private final ProductService productService;

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/product/list";
    }

    @GetMapping("/create")
    public String createProductPage(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", Category.values());
        return "admin/product/create";
    }

    @PostMapping
    public String createProduct(
            @ModelAttribute Product product,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        boolean hasImageFile = imageFile != null && !imageFile.isEmpty();
        String uploadedImageUrl = cloudinaryImageService.uploadProductImage(imageFile);
        if (uploadedImageUrl != null) {
            product.setImageUrl(uploadedImageUrl);
        } else if (hasImageFile) {
            // If user selected a file but upload failed, do not silently reuse stale URL.
            product.setImageUrl("/images/img001.png");
        } else if (product.getImageUrl() == null || product.getImageUrl().isBlank()) {
            product.setImageUrl("/images/img001.png");
        }
        productService.createProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String editProductPage(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", Category.values());
        return "admin/product/edit";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute Product product,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        Product existing = productService.getProductById(id);
        boolean hasImageFile = imageFile != null && !imageFile.isEmpty();
        String uploadedImageUrl = cloudinaryImageService.uploadProductImage(imageFile);

        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setDescription(product.getDescription());
        existing.setCategory(product.getCategory());
        existing.setBrand(product.getBrand());
        existing.setStockQuantity(product.getStockQuantity());
        if (uploadedImageUrl != null) {
            existing.setImageUrl(uploadedImageUrl);
        } else if (!hasImageFile && product.getImageUrl() != null && !product.getImageUrl().isBlank()) {
            existing.setImageUrl(product.getImageUrl());
        }
        productService.createProduct(existing); // save updated product
        return "redirect:/admin/products";
    }
}