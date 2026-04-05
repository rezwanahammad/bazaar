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

import com.example.bazaar.dto.ProductDto;
import com.example.bazaar.service.CartService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/cart")
    public String cart(Model model, Authentication auth) {
        List<ProductDto> cartItems = cartService.getCartDtosForUser(auth.getName());

        BigDecimal subtotal = cartItems.stream()
                .map(product -> product.getPrice() == null ? BigDecimal.ZERO : product.getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalQuantity = cartItems.size();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartSubtotal", subtotal);
        model.addAttribute("cartTotal", subtotal);
        model.addAttribute("cartQuantity", totalQuantity);
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam("productId") Long productId,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "quantity", required = false) Integer quantity,
            @RequestParam(value = "action", defaultValue = "add") String action,
            Authentication auth,
            RedirectAttributes redirectAttributes
    ) {
        cartService.addItem(
            auth.getName(),
            productId,
            null,
            null,
            null,
            size,
            quantity == null ? 1 : quantity
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
            @RequestParam(value = "size", required = false) String size,
            Authentication auth
    ) {
        cartService.removeItem(auth.getName(), productId, size);
        return "redirect:/cart";
    }
}
