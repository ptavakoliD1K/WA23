package com.WelfenHub.controllers;

import com.WelfenHub.models.Post;
import com.WelfenHub.models.User;
import com.WelfenHub.models.Comment;
import com.WelfenHub.services.PostService;
import com.WelfenHub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewPosts(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        return "posts";
    }

    @PostMapping("/create")
    public String createPost(@RequestParam String title, @RequestParam String content, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        postService.createPost(title, content, user);
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
