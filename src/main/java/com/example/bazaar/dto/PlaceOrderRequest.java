package com.example.bazaar.dto;

import com.example.bazaar.enums.PaymentMethod;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceOrderRequest {
    private PaymentMethod paymentMethod;
    private String transactionId;
    private String phone;
}
