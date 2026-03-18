package com.example.bazaar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bazaar.model.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByUserUsernameOrderByCreatedAtDesc(String username);

    Optional<OrderEntity> findByIdAndUserUsername(Long id, String username);
}
