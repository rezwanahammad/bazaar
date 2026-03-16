package com.example.bazaar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bazaar.enums.PaymentStatus;
import com.example.bazaar.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByStatusOrderByIdDesc(PaymentStatus status);

    List<Payment> findAllByOrderByIdDesc();
}
