package com.example.bazaar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.bazaar.enums.Category;
import com.example.bazaar.model.Product;
import com.example.bazaar.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(Product product){
        return productRepository.save(product);
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public List<Product> getActiveProducts() {
        return productRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    public List<Product> getActiveProductsByCategory(Category category) {
        return productRepository.findByCategoryAndActiveTrueOrderByCreatedAtDesc(category);
    }

    public Product getProductById(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getRelatedProducts(Product product) {
        List<Product> relatedProducts = productRepository
                .findTop4ByCategoryAndActiveTrueAndIdNotOrderByCreatedAtDesc(product.getCategory(), product.getId());

        if (!relatedProducts.isEmpty()) {
            return relatedProducts;
        }

        return productRepository.findTop4ByActiveTrueAndIdNotOrderByCreatedAtDesc(product.getId());
    }

    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }

    public List<Product> getProductsBySeller(String username) {
        return productRepository.findBySellerUsernameOrderByCreatedAtDesc(username);
    }

    public Product getProductByIdAndSeller(Long id, String username) {
        return productRepository.findByIdAndSellerUsername(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Product not found or access denied."));
    }

}