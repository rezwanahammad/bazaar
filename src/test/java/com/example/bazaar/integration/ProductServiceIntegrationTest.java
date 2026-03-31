package com.example.bazaar.integration;

import com.example.bazaar.enums.Category;
import com.example.bazaar.model.Product;
import com.example.bazaar.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Test
    void testCreateAndFetchProduct() {
        Product product = new Product();
        product.setName("Test Product");
        product.setCategory(Category.BAGGY_PANT);
        product.setActive(true);

        Product saved = productService.createProduct(product);

        assertNotNull(saved.getId());

        Product found = productService.getProductById(saved.getId());

        assertEquals("Test Product", found.getName());
    }

    @Test
    void testGetAllProducts() {
        Product p1 = new Product();
        p1.setName("P1");
        p1.setCategory(Category.BAGGY_PANT);
        p1.setActive(true);

        Product p2 = new Product();
        p2.setName("P2");
        p2.setCategory(Category.BAGGY_PANT);
        p2.setActive(true);

        productService.createProduct(p1);
        productService.createProduct(p2);

        List<Product> products = productService.getAllProducts();

        assertTrue(products.size() >= 2);
    }

    @Test
    void testGetActiveProducts() {
        Product active = new Product();
        active.setName("Active");
        active.setCategory(Category.BAGGY_PANT);
        active.setActive(true);

        Product inactive = new Product();
        inactive.setName("Inactive");
        inactive.setCategory(Category.BAGGY_PANT);
        inactive.setActive(false);

        productService.createProduct(active);
        productService.createProduct(inactive);

        List<Product> result = productService.getActiveProducts();

        assertTrue(result.stream().allMatch(p -> p.getActive()));
    }

    @Test
    void testGetProductsByCategory() {
        Product product = new Product();
        product.setName("CategoryTest");
        product.setCategory(Category.BAGGY_PANT);
        product.setActive(true);

        productService.createProduct(product);

        List<Product> result =
                productService.getActiveProductsByCategory(Category.BAGGY_PANT);

        assertFalse(result.isEmpty());
    }

    @Test
    void testDeleteProduct() {
        Product product = new Product();
        product.setName("ToDelete");
        product.setCategory(Category.BAGGY_PANT);
        product.setActive(true);

        Product saved = productService.createProduct(product);

        productService.deleteProduct(saved.getId());

        assertThrows(RuntimeException.class,
                () -> productService.getProductById(saved.getId()));
    }

    @Test
    void testGetRelatedProductsFallback() {
        Product product = new Product();
        product.setName("Main");
        product.setCategory(Category.BAGGY_PANT);
        product.setActive(true);

        Product saved = productService.createProduct(product);

        // Create other products
        for (int i = 0; i < 3; i++) {
            Product p = new Product();
            p.setName("Other " + i);
            p.setCategory(Category.BAGGY_PANT);
            p.setActive(true);
            productService.createProduct(p);
        }

        List<Product> related = productService.getRelatedProducts(saved);

        assertNotNull(related);
    }
}