package com.example.bazaar.controller.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bazaar.dto.ApiMessageResponse;
import com.example.bazaar.dto.CartItemRequest;
import com.example.bazaar.dto.ProductDto;
import com.example.bazaar.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class ApiCartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getCart(Authentication auth) {
        return ResponseEntity.ok(cartService.getCartDtosForUser(auth.getName()));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiMessageResponse> addItem(
            @RequestBody CartItemRequest request,
            Authentication auth
    ) {
        if (request.getProductId() == null) {
            throw new IllegalArgumentException("productId is required.");
        }

        int quantity = request.getQuantity() == null ? 1 : request.getQuantity();

        cartService.addItem(
                auth.getName(),
                request.getProductId(),
                null,
                null,
                null,
                request.getSize(),
                quantity
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiMessageResponse("Product added to cart."));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long productId,
            @RequestParam(value = "size", required = false) String size,
            Authentication auth
    ) {
        cartService.removeItem(auth.getName(), productId, size);
        return ResponseEntity.noContent().build();
    }
}
