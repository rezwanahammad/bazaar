package com.example.bazaar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bazaar.enums.Category;
import com.example.bazaar.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByActiveTrueOrderByCreatedAtDesc();

	List<Product> findByCategoryAndActiveTrueOrderByCreatedAtDesc(Category category);

	List<Product> findTop4ByCategoryAndActiveTrueAndIdNotOrderByCreatedAtDesc(Category category, Long id);

	List<Product> findTop4ByActiveTrueAndIdNotOrderByCreatedAtDesc(Long id);
}