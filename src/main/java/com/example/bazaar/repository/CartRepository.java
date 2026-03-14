package com.example.bazaar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.bazaar.model.CartItemEntity;

public interface CartRepository extends JpaRepository<CartItemEntity, Long> {

    List<CartItemEntity> findByUsernameOrderByIdAsc(String username);

    Optional<CartItemEntity> findByUsernameAndProductIdAndSize(String username, Long productId, String size);

    @Transactional
    void deleteByUsernameAndProductIdAndSize(String username, Long productId, String size);
}
