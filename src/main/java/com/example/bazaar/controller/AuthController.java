package com.example.bazaar.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.bazaar.model.User;
import com.example.bazaar.security.CustomUserDetails;
import com.example.bazaar.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("user", new User());
        return "register";
    }
    @PostMapping("/register")
    public String register(@ModelAttribute User user){
        userService.registerUser(user);
        return "redirect:/login";
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model){
        model.addAttribute("currentUser", userDetails.getUser());
        return "profile";
    }
}
