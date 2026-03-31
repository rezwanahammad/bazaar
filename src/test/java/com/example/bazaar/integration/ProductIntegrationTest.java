package com.example.bazaar.integration;

import com.example.bazaar.enums.Category;
import com.example.bazaar.model.Product;
import com.example.bazaar.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    private Product newProduct(String name, Category category, boolean active) {
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setActive(active);
        return product;
    }

    private Product saveProduct(String name, Category category, boolean active) {
        return productRepository.save(newProduct(name, category, active));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateAndFetchProduct() {
        assertDoesNotThrow(() -> mockMvc.perform(post("/admin/products")
                        .with(csrf())
                        .param("name", "Test Product")
                        .param("category", "BAGGY_PANT")
                        .param("active", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products")));

        Optional<Product> created = productRepository.findAll()
                .stream()
                .filter(p -> "Test Product".equals(p.getName()))
                .findFirst();
        assertTrue(created.isPresent());

        Long productId = created.get().getId();
        assertDoesNotThrow(() -> mockMvc.perform(get("/products/{id}", productId))
                .andExpect(status().isOk()));
    }

    @Test
    void testGetAllProducts() {
        saveProduct("P1", Category.BAGGY_PANT, true);
        saveProduct("P2", Category.BAGGY_PANT, true);

        MvcResult result = assertDoesNotThrow(() -> mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andReturn());

        @SuppressWarnings("unchecked")
        List<?> products = (List<?>) result.getModelAndView().getModel().get("products");
        assertNotNull(products);
        assertTrue(products.size() >= 2);
    }

    @Test
    void testGetActiveProducts() {
        saveProduct("Active", Category.BAGGY_PANT, true);
        saveProduct("Inactive", Category.BAGGY_PANT, false);

        MvcResult result = assertDoesNotThrow(() -> mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andReturn());

        @SuppressWarnings("unchecked")
        List<com.example.bazaar.dto.ProductDto> products =
                (List<com.example.bazaar.dto.ProductDto>) result.getModelAndView().getModel().get("products");
        assertNotNull(products);
        assertTrue(products.stream().allMatch(com.example.bazaar.dto.ProductDto::getActive));
    }

    @Test
    void testGetProductsByCategory() {
        saveProduct("CategoryTest", Category.BAGGY_PANT, true);
        saveProduct("DifferentCategory", Category.OVERSIZED_SHIRT, true);

        MvcResult result = assertDoesNotThrow(() -> mockMvc.perform(get("/products").param("category", "baggy-pant"))
                .andExpect(status().isOk())
                .andReturn());

        @SuppressWarnings("unchecked")
        List<com.example.bazaar.dto.ProductDto> products =
                (List<com.example.bazaar.dto.ProductDto>) result.getModelAndView().getModel().get("products");
        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertTrue(products.stream().allMatch(p -> p.getCategory() == Category.BAGGY_PANT));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteProduct() {
        Product saved = saveProduct("ToDelete", Category.BAGGY_PANT, true);

        assertDoesNotThrow(() -> mockMvc.perform(post("/admin/products/{id}/delete", saved.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products")));

        assertFalse(productRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void testGetRelatedProductsFallback() {
        Product saved = saveProduct("Main", Category.BAGGY_PANT, true);

        // Create other active products for fallback/related list.
        for (int i = 0; i < 3; i++) {
            saveProduct("Other " + i, Category.BAGGY_PANT, true);
        }

        MvcResult result = assertDoesNotThrow(() -> mockMvc.perform(get("/products/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andReturn());

        @SuppressWarnings("unchecked")
        List<com.example.bazaar.dto.ProductDto> related =
                (List<com.example.bazaar.dto.ProductDto>) result.getModelAndView().getModel().get("relatedProducts");

        assertNotNull(related);
    }
}