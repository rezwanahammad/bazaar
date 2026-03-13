package com.example.bazaar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.bazaar.service.ProductService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("products", productService.getActiveProducts().stream().limit(8).toList());
        return "home";
    }

    @GetMapping("/error")
    public String error(){
        return "error";
    }
}
