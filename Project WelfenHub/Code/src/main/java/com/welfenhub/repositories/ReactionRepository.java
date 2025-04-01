package com.welfenhub.repositories;

import com.welfenhub.models.Reaction;
import com.welfenhub.models.User;
import com.welfenhub.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserAndPost(User user, Post post);
    List<Reaction> findByPost(Post post);
}