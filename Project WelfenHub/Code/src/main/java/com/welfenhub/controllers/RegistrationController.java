package com.welfenhub.controllers;

import com.welfenhub.models.User;
import com.welfenhub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import org.springframework.validation.BindingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        logger.info("Showing registration form");
        model.addAttribute("user", new User());
        return "register";
    }

    @Transactional
    @PostMapping("/register")
    public String registerUser(
            @Valid User user,
            BindingResult bindingResult,
            Model model,
            @RequestParam String confirmPassword) {

        logger.info("Registering user with username: {}", user.getUsername());

        if (bindingResult.hasErrors()) {
            logger.error("Validation errors while registering user: {}", user.getUsername());
            return "register";
        }

        if (!user.getPassword().equals(confirmPassword)) {
            logger.error("Passwords do not match for user: {}", user.getUsername());
            model.addAttribute("error", "Die Passwörter sind nicht identisch. Bitte versuche es erneut");
            return "register";
        }

        if (!user.getEmail().contains("welfenakademie")) {
            logger.error("Invalid Email for User: {}", user.getUsername());
            model.addAttribute("error", "Bitte nutze eine Welfenakademie E-Mail");
            return "register";
        }

        try {
            logger.info("Attempting to save user: {}", user.getUsername());
            userService.save(user);
            logger.info("User registered successfully: {}", user.getUsername());
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("Error registering user: {}", user.getUsername(), e);
            model.addAttribute("error", "Username oder E-Mail existiert bereits.");
            return "register";
        }
    }
}
