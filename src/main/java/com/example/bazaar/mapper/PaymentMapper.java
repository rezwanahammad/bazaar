package com.example.bazaar.mapper;

import org.springframework.stereotype.Component;

import com.example.bazaar.dto.PaymentDto;
import com.example.bazaar.model.Payment;

@Component
public class PaymentMapper {

    public PaymentDto toDto(Payment entity) {
        if (entity == null) {
            return null;
        }

        return PaymentDto.builder()
                .id(entity.getId())
                .orderId(entity.getOrder() == null ? null : entity.getOrder().getId())
                .method(entity.getMethod())
                .transactionId(entity.getTransactionId())
                .phone(entity.getPhone())
                .status(entity.getStatus())
                .paidAt(entity.getPaidAt())
                .verifiedAt(entity.getVerifiedAt())
                .adminNote(entity.getAdminNote())
                .build();
    }
}
