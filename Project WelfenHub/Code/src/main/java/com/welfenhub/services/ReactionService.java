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

    @Transactional
    public int toggleReaction(Long postId, String username) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post nicht gefunden"));
        User user = userRepo.findByUsername(username);

        Optional<Reaction> existing = reactionRepo.findByUserAndPost(user, post);
        if (existing.isPresent()) {
            reactionRepo.delete(existing.get());
        } else {
            Reaction reaction = new Reaction();
            reaction.setUser(user);
            reaction.setPost(post);
            reactionRepo.save(reaction);
        }

        return reactionRepo.findByPost(post).size();
    }
}