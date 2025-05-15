package com.welfenhub.services;

import com.welfenhub.models.Post;
import com.welfenhub.models.User;
import com.welfenhub.models.Reaction;
import com.welfenhub.repositories.PostRepository;
import com.welfenhub.repositories.ReactionRepository;
import com.welfenhub.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ReactionService {

    @Autowired
    private ReactionRepository reactionRepo;

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Transactional
    public Map<String, Object> toggleReaction(Long postId, String username) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post nicht gefunden"));
        User user = userRepo.findByUsername(username);

        Optional<Reaction> existing = reactionRepo.findByUserAndPost(user, post);
        boolean likedByUser;

        if (existing.isPresent()) {
            reactionRepo.delete(existing.get());
            likedByUser = false;
        } else {
            Reaction reaction = new Reaction();
            reaction.setUser(user);
            reaction.setPost(post);
            reactionRepo.save(reaction);

            // Jetzt reload nach Save → garantiert aktuell
            likedByUser = true;
        }

        // Reaktionen neu laden, um korrekten Count zu erhalten
        int newCount = reactionRepo.findByPost(post).size();

        // 🔴 WebSocket für Live-Updates senden
        Map<String, Object> wsPayload = new HashMap<>();
        wsPayload.put("postId", postId);
        wsPayload.put("reactionCount", newCount);
        messagingTemplate.convertAndSend("/topic/reactions", wsPayload);

        // ✅ REST-Antwort zurückgeben
        Map<String, Object> result = new HashMap<>();
        result.put("reactionCount", newCount);
        result.put("likedByUser", likedByUser);
        return result;
    }
}
