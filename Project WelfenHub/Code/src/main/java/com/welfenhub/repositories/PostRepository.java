package com.welfenhub.repositories;

import com.welfenhub.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findByCourse(String course); // Methode zum Abrufen von Posts nach Kurs
    Post findTopByCourseOrderByCreatedAtDesc(String course);

    List<Post> findByTitleContainingIgnoreCase(String title);

}
