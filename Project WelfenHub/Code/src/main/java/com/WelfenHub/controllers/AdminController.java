package com.WelfenHub.controllers;

import com.WelfenHub.models.User;
import com.WelfenHub.models.UserRole;
import com.WelfenHub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/AdminDashboard")
    public String dashboard(Model model) {
        long userCount = userService.getUserCount();
        List<User> users = userService.findAllUsers();
        model.addAttribute("userCount", userCount);
        model.addAttribute("users", users);
        return "admin/AdminDashboard";
    }

    @PostMapping("/updateRole")
    public String updateUserRole(@RequestParam Long userId, @RequestParam UserRole role) {
        userService.updateUserRole(userId, role);
        return "redirect:/admin/AdminDashboard";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam Long userId) {
        userService.deleteUserById(userId);
        return "redirect:/admin/AdminDashboard";
    }
}