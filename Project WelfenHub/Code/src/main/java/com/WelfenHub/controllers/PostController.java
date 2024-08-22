package com.WelfenHub.controllers;

import com.WelfenHub.models.Post;
import com.WelfenHub.models.User;
import com.WelfenHub.services.PostService;
import com.WelfenHub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewPosts(@RequestParam(value = "category", required = false) String category, Model model) {
        List<Post> posts;
        if (category != null && !category.isEmpty()) {
            posts = postService.getPostsByCategory(category);
        } else {
            posts = postService.getAllPosts();
        }
        model.addAttribute("posts", posts);
        return "posts";
    }

    @PostMapping("/create")
    public String createPost(@RequestParam String title, @RequestParam String content, @RequestParam String category, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        postService.createPost(title, content, category, user);
        return "redirect:/posts";
    }

    @PostMapping("/comment")
    public String addComment(@RequestParam Long postId, @RequestParam String content, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        postService.addComment(postId, content, user);
        return "redirect:/posts";
    }
}
