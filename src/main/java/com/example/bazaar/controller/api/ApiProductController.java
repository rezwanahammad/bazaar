package com.example.bazaar.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bazaar.dto.ProductDto;
import com.example.bazaar.enums.Category;
import com.example.bazaar.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ApiProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts(
            @RequestParam(value = "category", required = false) String category
    ) {
        if (category == null || category.isBlank()) {
            return ResponseEntity.ok(productService.getActiveProductDtos());
        }

        Category parsed = Category.valueOf(category.trim().replace('-', '_').toUpperCase());
        return ResponseEntity.ok(productService.getActiveProductDtosByCategory(parsed));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductDtoById(id));
    }
}
