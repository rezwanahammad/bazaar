package com.example.bazaar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bazaar.service.PaymentAdminService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/payments")
public class AdminPaymentController {

    private final PaymentAdminService paymentAdminService;

    @GetMapping
    public String paymentList(Model model) {
        model.addAttribute("payments", paymentAdminService.getAllPaymentDtos());
        return "admin/payments/list";
    }

    @PostMapping("/{paymentId}/approve")
    public String approve(@PathVariable Long paymentId, RedirectAttributes redirectAttributes) {
        paymentAdminService.approvePayment(paymentId);
        redirectAttributes.addFlashAttribute("paymentMessage", "Payment approved successfully.");
        return "redirect:/admin/payments";
    }

    @PostMapping("/{paymentId}/reject")
    public String reject(
            @PathVariable Long paymentId,
            @RequestParam(value = "reason", required = false) String reason,
            RedirectAttributes redirectAttributes
    ) {
        paymentAdminService.rejectPayment(paymentId, reason);
        redirectAttributes.addFlashAttribute("paymentMessage", "Payment rejected.");
        return "redirect:/admin/payments";
    }
}
