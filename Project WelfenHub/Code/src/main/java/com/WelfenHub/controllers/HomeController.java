package com.WelfenHub.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";  // This refers to src/main/resources/templates/index.html
    }

    @GetMapping("/login")
    public String login() {
        return "login";  // This refers to src/main/resources/templates/login.html
    }
}
