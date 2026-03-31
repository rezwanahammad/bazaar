package com.example.bazaar.dto;

import java.time.LocalDateTime;

import com.example.bazaar.enums.PaymentMethod;
import com.example.bazaar.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    private Long id;
    private Long orderId;
    private PaymentMethod method;
    private String transactionId;
    private String phone;
    private PaymentStatus status;
    private LocalDateTime paidAt;
    private LocalDateTime verifiedAt;
    private String adminNote;
}
