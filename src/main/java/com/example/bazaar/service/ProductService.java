package com.example.bazaar.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.bazaar.dto.ProductDto;
import com.example.bazaar.enums.Category;
import com.example.bazaar.exception.ResourceNotFoundException;
import com.example.bazaar.mapper.ProductMapper;
import com.example.bazaar.model.Product;
import com.example.bazaar.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Product createProduct(Product product){
        return productRepository.save(product);
    }

    public ProductDto createProduct(ProductDto productDto) {
        Product savedProduct = productRepository.save(productMapper.toEntity(productDto));
        return productMapper.toDto(savedProduct);
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public List<ProductDto> getAllProductDtos() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<Product> getActiveProducts() {
        return productRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    public List<ProductDto> getActiveProductDtos() {
        return productRepository.findByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<Product> getActiveProductsByCategory(Category category) {
        return productRepository.findByCategoryAndActiveTrueOrderByCreatedAtDesc(category);
    }

    public List<ProductDto> getActiveProductDtosByCategory(Category category) {
        return productRepository.findByCategoryAndActiveTrueOrderByCreatedAtDesc(category)
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public Product getProductById(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found."));
    }

    public ProductDto getProductDtoById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found."));
        return productMapper.toDto(product);
    }

    public List<Product> getRelatedProducts(Product product) {
        List<Product> relatedProducts = productRepository
                .findTop4ByCategoryAndActiveTrueAndIdNotOrderByCreatedAtDesc(product.getCategory(), product.getId());

        if (!relatedProducts.isEmpty()) {
            return relatedProducts;
        }

        return productRepository.findTop4ByActiveTrueAndIdNotOrderByCreatedAtDesc(product.getId());
    }

    public List<ProductDto> getRelatedProductDtos(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        return getRelatedProducts(product)
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }

    public List<Product> getProductsBySeller(String username) {
        return productRepository.findBySellerUsernameOrderByCreatedAtDesc(username);
    }

    public List<ProductDto> getProductsBySellerDtos(String username) {
        return productRepository.findBySellerUsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ProductDto> getActiveProductsBySellerDtos(String username) {
        return productRepository.findBySellerUsernameOrderByCreatedAtDesc(username)
                .stream()
                .filter(Product::getActive)
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public Product getProductByIdAndSeller(Long id, String username) {
        return productRepository.findByIdAndSellerUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found or access denied."));
    }

    public ProductDto getProductDtoByIdAndSeller(Long id, String username) {
        Product product = productRepository.findByIdAndSellerUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found or access denied."));
        return productMapper.toDto(product);
    }

}