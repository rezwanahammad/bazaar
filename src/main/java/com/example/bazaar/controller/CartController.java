package com.example.bazaar.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bazaar.model.CartItemEntity;
import com.example.bazaar.model.Product;
import com.example.bazaar.service.CartService;
import com.example.bazaar.service.ProductService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    @GetMapping("/cart")
    public String cart(Model model, Authentication auth) {
    List<CartItemEntity> cartItems = cartService.getCartForUser(auth.getName());

    BigDecimal subtotal = cartItems.stream()
        .map(CartItemEntity::getLineTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    int totalQuantity = cartItems.stream()
        .mapToInt(CartItemEntity::getQuantity)
        .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartSubtotal", subtotal);
        model.addAttribute("cartTotal", subtotal);
        model.addAttribute("cartQuantity", totalQuantity);
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam("productId") Long productId,
            @RequestParam("size") String size,
            @RequestParam("quantity") Integer quantity,
            @RequestParam(value = "action", defaultValue = "add") String action,
            Authentication auth,
            RedirectAttributes redirectAttributes
    ) {
        Product product = productService.getProductById(productId);
        String normalizedSize = (size == null || size.isBlank()) ? "M-42/27" : size.trim();
        int normalizedQuantity = quantity == null ? 1 : Math.max(1, quantity);

        cartService.addItem(
            auth.getName(),
            product.getId(),
            product.getName(),
            product.getImageUrl(),
            product.getPrice(),
            normalizedSize,
            normalizedQuantity
        );

        redirectAttributes.addFlashAttribute("cartSuccess", "Product added to cart successfully.");

        if ("order".equalsIgnoreCase(action)) {
            return "redirect:/cart";
        }
        return "redirect:/products/" + productId;
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(
            @RequestParam("productId") Long productId,
            @RequestParam("size") String size,
            Authentication auth
    ) {
        cartService.removeItem(auth.getName(), productId, size);
        return "redirect:/cart";
    }
}
