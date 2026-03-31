package com.example.bazaar.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bazaar.dto.CartItemDto;
import com.example.bazaar.dto.OrderDto;
import com.example.bazaar.enums.PaymentMethod;
import com.example.bazaar.service.CartService;
import com.example.bazaar.service.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class CheckoutController {

    private static final String MANUAL_PAYMENT_NUMBER = "01XXXXXXXXX";

    private final CartService cartService;
    private final OrderService orderService;

    @GetMapping("/checkout")
    public String checkoutPage(Model model, Authentication auth, RedirectAttributes redirectAttributes) {
        List<CartItemDto> cartItems = cartService.getCartDtosForUser(auth.getName());
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("cartSuccess", "Your cart is empty. Add products first.");
            return "redirect:/cart";
        }

        BigDecimal total = cartItems.stream()
                .map(CartItemDto::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", total);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("manualPaymentNumber", MANUAL_PAYMENT_NUMBER);
        return "checkout";
    }

    @PostMapping("/checkout/place")
    public String placeOrder(
            @RequestParam("paymentMethod") PaymentMethod paymentMethod,
            @RequestParam(value = "transactionId", required = false) String transactionId,
            @RequestParam(value = "phone", required = false) String phone,
            Authentication auth,
            RedirectAttributes redirectAttributes
    ) {
        try {
            OrderDto order = orderService.placeOrderDto(auth.getName(), paymentMethod, transactionId, phone);
            return "redirect:/orders/" + order.getId() + "/success";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("checkoutError", ex.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/orders")
    public String myOrders(Model model, Authentication auth) {
        model.addAttribute("orders", orderService.getOrderDtosForUser(auth.getName()));
        return "orders/list";
    }

    @GetMapping("/orders/{orderId}/success")
    public String orderSuccess(@PathVariable Long orderId, Authentication auth, Model model) {
        model.addAttribute("order", orderService.getOrderDtoForUser(orderId, auth.getName()));
        return "orders/success";
    }
}
