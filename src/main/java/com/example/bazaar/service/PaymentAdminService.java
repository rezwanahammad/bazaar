package com.example.bazaar.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bazaar.enums.OrderStatus;
import com.example.bazaar.enums.PaymentStatus;
import com.example.bazaar.model.Payment;
import com.example.bazaar.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentAdminService {

    private final PaymentRepository paymentRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public void approvePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found."));

        payment.setStatus(PaymentStatus.APPROVED);
        payment.setVerifiedAt(LocalDateTime.now());
        payment.getOrder().setStatus(OrderStatus.PAID);
    }

    @Transactional
    public void rejectPayment(Long paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found."));

        payment.setStatus(PaymentStatus.REJECTED);
        payment.setVerifiedAt(LocalDateTime.now());
        payment.setAdminNote((reason == null || reason.isBlank()) ? "Rejected by admin" : reason.trim());
        payment.getOrder().setStatus(OrderStatus.CANCELLED);
    }
}
