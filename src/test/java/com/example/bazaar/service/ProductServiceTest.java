package com.example.bazaar.service;

import com.example.bazaar.enums.Category;
import com.example.bazaar.exception.ResourceNotFoundException;
import com.example.bazaar.mapper.ProductMapper;
import com.example.bazaar.model.Product;
import com.example.bazaar.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productRepository;
    private ProductService productService;

    @BeforeEach
    void setup() {
        productRepository = mock(ProductRepository.class);
        productService = new ProductService(productRepository, new ProductMapper());
    }

    @Test
    void testCreateProduct() {
        Product product = new Product();
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.createProduct(product);

        assertEquals(product, result);
        verify(productRepository).save(product);
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = List.of(new Product(), new Product());
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
    }

    @Test
    void testGetProductByIdSuccess() {
        Product product = new Product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertEquals(product, result);
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(1L));
    }

    @Test
    void testGetRelatedProducts_CategoryMatch() {
        Product product = new Product();
        product.setId(1L);
        product.setCategory(Category.BAGGY_PANT);

        List<Product> related = List.of(new Product(), new Product());

        when(productRepository
                .findTop4ByCategoryAndActiveTrueAndIdNotOrderByCreatedAtDesc(
                        Category.BAGGY_PANT, 1L))
                .thenReturn(related);

        List<Product> result = productService.getRelatedProducts(product);

        assertEquals(2, result.size());
        verify(productRepository, never())
                .findTop4ByActiveTrueAndIdNotOrderByCreatedAtDesc(any());
    }

    @Test
    void testGetRelatedProducts_Fallback() {
        Product product = new Product();
        product.setId(1L);
        product.setCategory(Category.BAGGY_PANT);

        when(productRepository
                .findTop4ByCategoryAndActiveTrueAndIdNotOrderByCreatedAtDesc(
                        Category.BAGGY_PANT, 1L))
                .thenReturn(List.of());

        when(productRepository
                .findTop4ByActiveTrueAndIdNotOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(new Product()));

        List<Product> result = productService.getRelatedProducts(product);

        assertEquals(1, result.size());
        verify(productRepository)
                .findTop4ByActiveTrueAndIdNotOrderByCreatedAtDesc(1L);
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void testGetProductsBySeller() {
        List<Product> products = List.of(new Product());

        when(productRepository
                .findBySellerUsernameOrderByCreatedAtDesc("seller"))
                .thenReturn(products);

        List<Product> result = productService.getProductsBySeller("seller");

        assertEquals(1, result.size());
    }

    @Test
    void testGetProductByIdAndSellerSuccess() {
        Product product = new Product();

        when(productRepository.findByIdAndSellerUsername(1L, "seller"))
                .thenReturn(Optional.of(product));

        Product result = productService.getProductByIdAndSeller(1L, "seller");

        assertEquals(product, result);
    }

    @Test
    void testGetProductByIdAndSellerFail() {
        when(productRepository.findByIdAndSellerUsername(1L, "seller"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductByIdAndSeller(1L, "seller"));
    }
}