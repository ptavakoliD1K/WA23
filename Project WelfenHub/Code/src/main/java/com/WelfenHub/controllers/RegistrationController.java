package com.WelfenHub.controllers;

import com.WelfenHub.models.User;
import com.WelfenHub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;


@Controller
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm() {
        logger.info("Showing registration form");
        return "register";
    }
    @Transactional
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, @RequestParam String confirmPassword, @RequestParam String email, @RequestParam String fullName, Model model) {
        logger.info("Registering user with username: {}", username);

        if (!password.equals(confirmPassword)) {
            logger.error("Passwords do not match for user: {}", username);
            model.addAttribute("error", "Passwords do not match. Please try again.");
            return "register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setFullName(fullName);

        try {
            logger.info("Attempting to save user: {}", username);
            userService.save(user);
            logger.info("User registered successfully: {}", username);
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("Error registering user: {}", username, e);
            model.addAttribute("error", "Username or email already exists. Please choose another one.");
            return "register";
        }
    }
}
