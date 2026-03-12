package com.example.bazaar.controller;

import org.springframework.stereotype.Controller;
import org. springframework.web.bind.annotation.GetMapping;

@Controller
public abstract class HomeController {
    @GetMapping("/")
    public String home(){
        return "home";
    }
}
