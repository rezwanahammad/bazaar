package com.example.bazaar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bazaar.service.ProductService;
import com.example.bazaar.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final UserService userService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("productCount", productService.getAllProducts().size());
        model.addAttribute("userCount", userService.getAllUsers().size());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users/list";
    }

    @PostMapping("/users/{id}/make-admin")
    public String makeAdmin(@PathVariable Long id) {
        userService.setRole(id, "ROLE_ADMIN");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/remove-admin")
    public String removeAdmin(@PathVariable Long id) {
        userService.setRole(id, "ROLE_USER");
        return "redirect:/admin/users";
    }
}
