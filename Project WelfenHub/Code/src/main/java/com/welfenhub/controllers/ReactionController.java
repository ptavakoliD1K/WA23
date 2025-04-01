package com.welfenhub.controllers;

import com.welfenhub.services.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/reactions")
public class ReactionController {

    @Autowired
    private ReactionService reactionService;

    @PostMapping("/{postId}")
    public ResponseEntity<?> reactToPost(@PathVariable Long postId, Principal principal) {
        String username = principal.getName();
        try {
            int count = reactionService.toggleReaction(postId, username);

            Map<String, Object> response = new HashMap<>();
            response.put("postId", postId);
            response.put("reactionCount", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fehler: " + e.getMessage());
        }
    }
}
