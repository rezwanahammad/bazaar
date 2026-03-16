package com.example.bazaar.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bazaar.enums.Category;
import com.example.bazaar.model.Product;
import com.example.bazaar.service.CloudinaryImageService;
import com.example.bazaar.service.ProductService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/seller/products")
public class SellerProductController {

    private final ProductService productService;
    private final CloudinaryImageService cloudinaryImageService;

    @GetMapping
    public String listProducts(Model model, Authentication auth) {
        model.addAttribute("products", productService.getProductsBySeller(auth.getName()));
        return "seller/products/list";
    }

    @GetMapping("/create")
    public String createProductPage(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", Category.values());
        return "seller/products/create";
    }

    @PostMapping
    public String createProduct(
            @ModelAttribute Product product,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Authentication auth,
            RedirectAttributes redirectAttributes
    ) {
        boolean hasImageFile = imageFile != null && !imageFile.isEmpty();
        String uploadedImageUrl = cloudinaryImageService.uploadProductImage(imageFile);
        if (uploadedImageUrl != null) {
            product.setImageUrl(uploadedImageUrl);
        } else if (hasImageFile) {
            product.setImageUrl("/images/img001.png");
        } else if (product.getImageUrl() == null || product.getImageUrl().isBlank()) {
            product.setImageUrl("/images/img001.png");
        }
        product.setSellerUsername(auth.getName());
        product.setActive(true);
        productService.createProduct(product);
        redirectAttributes.addFlashAttribute("successMessage", "Product created successfully.");
        return "redirect:/seller/products";
    }

    @GetMapping("/{id}/edit")
    public String editProductPage(
            @PathVariable Long id,
            Authentication auth,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            model.addAttribute("product", productService.getProductByIdAndSeller(id, auth.getName()));
            model.addAttribute("categories", Category.values());
            return "seller/products/edit";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/seller/products";
        }
    }

    @PostMapping("/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute Product product,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Authentication auth,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Product existing = productService.getProductByIdAndSeller(id, auth.getName());
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
            productService.createProduct(existing);
            redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/seller/products";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(
            @PathVariable Long id,
            Authentication auth,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Product existing = productService.getProductByIdAndSeller(id, auth.getName());
            productService.deleteProduct(existing.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/seller/products";
    }
}
