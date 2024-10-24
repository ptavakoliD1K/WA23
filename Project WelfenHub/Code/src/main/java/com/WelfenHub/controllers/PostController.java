package com.WelfenHub.controllers;

import com.WelfenHub.models.Post;
import com.WelfenHub.models.User;
import com.WelfenHub.models.Comment;
import com.WelfenHub.services.PostService;
import com.WelfenHub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    // Posts anzeigen, mit oder ohne Kursfilter
    @GetMapping
    public String viewPosts(@RequestParam(value = "course", required = false) String course, Model model) {
        List<Post> posts;
        if (course != null && !course.isEmpty()) {
            posts = postService.getPostsByCourse(course);
        } else {
            posts = postService.getAllPosts();
        }
        model.addAttribute("posts", posts);
        return "posts";
    }

    // Neuen Post erstellen
    @PostMapping("/create")
    public String createPost(@RequestParam String title,
                             @RequestParam String content,
                             @RequestParam String course,
                             @RequestParam int semester,
                             @RequestParam String subject,  // Neu hinzugefügt
                             Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        postService.createPost(title, content, course, semester, subject, user); // subject übergeben
        return "redirect:/forum/" + subject + "/" + semester + "/course/" + course;
    }

    // Kommentar zu einem Post hinzufügen
    @PostMapping("/comment")
    public String addComment(@RequestParam Long postId, @RequestParam String content, Principal principal) {
        Post post = postService.findById(postId);  // Den zugehörigen Post abrufen
        String username = principal.getName();
        User user = userService.findByUsername(username);
        postService.addComment(postId, content, user);

        // Die Werte für subject, semester und course aus dem Post-Objekt abrufen
        String subject = post.getSubject();
        int semester = post.getSemester();
        String course = post.getCourse();

        return "redirect:/forum/" + subject + "/" + semester + "/course/" + course;
    }

    // Einzelnen Post und zugehörige Kommentare anzeigen
    @GetMapping("/{postId}")
    public String viewPostWithComments(@PathVariable Long postId, Model model) {
        Post post = postService.findById(postId);
        List<Comment> comments = post.getComments();  // Kommentare laden
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "post-detail"; // Template für die Detailansicht eines Posts mit Kommentaren
    }

    // Post bearbeiten (Formular anzeigen)
    @GetMapping("/edit/{postId}")
    public String showEditPostForm(@PathVariable Long postId, Principal principal, Model model) {
        Post post = postService.findById(postId);
        User currentUser = userService.findByUsername(principal.getName());

        if (post.getUser().equals(currentUser)) {
            model.addAttribute("post", post);
            return "edit-post";
        } else {
            return "redirect:/posts";
        }
    }

    // Post bearbeiten (Absenden)
    @PostMapping("/edit/{postId}")
    public String editPost(@PathVariable Long postId, @RequestParam String title, @RequestParam String content, Principal principal) {
        Post post = postService.findById(postId);
        User currentUser = userService.findByUsername(principal.getName());

        if (post.getUser().equals(currentUser)) {
            post.setTitle(title);
            post.setContent(content);
            postService.save(post);
            return "redirect:/posts";
        } else {
            return "redirect:/posts";
        }
    }

    // Kommentar bearbeiten (Formular anzeigen)
    @GetMapping("/comment/edit/{commentId}")
    public String showEditCommentForm(@PathVariable Long commentId, Principal principal, Model model) {
        Comment comment = postService.findCommentById(commentId);
        User currentUser = userService.findByUsername(principal.getName());

        if (comment.getUser().equals(currentUser)) {
            model.addAttribute("comment", comment);
            return "edit-comment";
        } else {
            return "redirect:/posts";
        }
    }

    // Kommentar bearbeiten (Absenden)
    @PostMapping("/comment/edit/{commentId}")
    public String editComment(@PathVariable Long commentId, @RequestParam String content, Principal principal) {
        Comment comment = postService.findCommentById(commentId);
        User currentUser = userService.findByUsername(principal.getName());

        if (comment.getUser().equals(currentUser)) {
            comment.setContent(content);
            postService.saveComment(comment);
            return "redirect:/posts";
        } else {
            return "redirect:/posts";
        }
    }

    // Post löschen
    @PostMapping("/delete/{postId}")
    public String deletePost(@PathVariable Long postId, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            postService.deletePost(postId);
        }
        return "redirect:/posts";
    }

    // Kommentar löschen
    @PostMapping("/comment/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            postService.deleteComment(commentId);
        }

        return "redirect:/posts";
    }

    @GetMapping("/search")
    public String searchPosts(@RequestParam("query") String query, Model model) {
        List<Post> posts = postService.searchPostsByTitle(query);
        model.addAttribute("posts", posts);

        // Display the search results in the same posts view
        return "subject";  // Assuming this is your posts view template
    }


}
