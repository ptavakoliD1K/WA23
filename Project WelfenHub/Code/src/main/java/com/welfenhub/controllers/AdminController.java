package com.welfenhub.controllers;

import com.welfenhub.models.User;
import com.welfenhub.models.UserRole;
import com.welfenhub.services.UserService;
import com.welfenhub.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private SessionRegistry sessionRegistry;

    @GetMapping("/AdminDashboard")
    public String dashboard(Model model) {
        long userCount = userService.getUserCount();
        long postCount = postService.getTotalPostCount();
        int onlineUsers = getOnlineUsersCount();

        List<User> users = userService.findAllUsers();

        model.addAttribute("userCount", userCount);
        model.addAttribute("postCount", postCount);
        model.addAttribute("onlineUsers", onlineUsers);
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

    @GetMapping("/admin/generateFakePosts")
    @ResponseBody
    public String generateFakePosts() {
        User user = userService.findAllUsers().get(1); // nehme irgendeinen vorhandenen User

        LocalDateTime startDate = LocalDateTime.now().minusMonths(12);

        for (int i = 0; i < 100; i++) {
            postService.createPost(
                    "Testpost " + i,
                    "Inhalt für Testpost " + i,
                    "Testkurs",
                    1,
                    "Sonstiges",
                    user
            ).setCreatedAt(startDate.plusDays(i * 3)); // Alle 3 Tage ein neuer Post
        }

        return "100 Testposts erfolgreich generiert!";
    }


    private int getOnlineUsersCount() {
        return sessionRegistry.getAllPrincipals().size();
    }

    @GetMapping("/posts/stats")
    @ResponseBody
    public List<Object[]> getPostStats(@RequestParam String period) {
        LocalDateTime now = LocalDateTime.now();
        switch (period) {
            case "day":
                return postService.getPostCountByHour(now.minusDays(1), now);
            case "week":
                return postService.getPostCountByDay(now.minusDays(7), now);
            case "month":
                return postService.getPostCountByDay(now.minusDays(30), now);
            case "year":
                return postService.getPostCountByMonth(now.minusMonths(12), now);

            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }
    }

}
