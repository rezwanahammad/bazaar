package com.example.bazaar.controller.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bazaar.dto.OrderDto;
import com.example.bazaar.dto.PlaceOrderRequest;
import com.example.bazaar.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class ApiOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getMyOrders(Authentication auth) {
        return ResponseEntity.ok(orderService.getOrderDtosForUser(auth.getName()));
    }

    @PostMapping
    public ResponseEntity<OrderDto> placeOrder(
            @RequestBody PlaceOrderRequest request,
            Authentication auth
    ) {
        OrderDto order = orderService.placeOrderDto(
                auth.getName(),
                request.getPaymentMethod(),
                request.getTransactionId(),
                request.getPhone()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}
