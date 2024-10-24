package com.WelfenHub.controllers;

import com.WelfenHub.models.User;
import com.WelfenHub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, Model model) {
        logger.info("Attempting to log in user: {}", username);
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        boolean authenticated = userService.authenticate(user);
        if (authenticated) {
            logger.info("User logged in successfully: {}", username);
            return "redirect:/";  // Redirect to home page
        } else {
            logger.info("User login failed: {}", username);
            model.addAttribute("error", "Invalid username or password. Please try again.");
            return "login";  // Redirect back to login page with error message
        }
    }

    @GetMapping("/passwordreset")
    public String showPasswordResetForm(Model model) {
        model.addAttribute("user", new User()); // Assuming User is your model class
        return "passwordreset";
    }

    @GetMapping("/api/user-status")
    public Map<String, Object> getUserStatus() {
        Map<String, Object> response = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser");
        response.put("loggedIn", isLoggedIn);
        return response;
    }
}
