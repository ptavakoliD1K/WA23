package com.welfenhub.services;

import com.welfenhub.models.Post;
import com.welfenhub.models.User;
import com.welfenhub.models.Comment;
import com.welfenhub.repositories.UserRepository;
import com.welfenhub.repositories.PostRepository;
import com.welfenhub.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;


import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Post> getPostsByCourse(String course) {
        return postRepository.findByCourse(course);
    }

    public long getTotalPostCount() {
        return postRepository.count();
    }

    public Post createPost(String title, String content, String course, int semester, String subject, User user) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCourse(course);
        post.setSemester(semester);
        post.setSubject(subject); // subject hinzufügen
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }


    public Comment addComment(Long postId, String content, String username) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            // Benutzer anhand des Benutzernamens abrufen
            User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }

            // Kommentar erstellen und speichern
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setPost(post);
            comment.setUser(user);
            comment.setCreatedDate(LocalDateTime.now());
            Comment savedComment = commentRepository.save(comment);
            System.out.println("Saved Comment: " + savedComment);

            return savedComment;
        } else {
            throw new IllegalArgumentException("Invalid post ID");
        }
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
    }

    public void save(Post post) {
        postRepository.save(post);
    }

    public Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
    }

    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public Post getLatestPostForSubject(String subject) {
        return postRepository.findTopByCourseOrderByCreatedAtDesc(subject);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Post> searchPostsByTitle(String query) {
        return postRepository.findByTitleContainingIgnoreCase(query);
    }

    public List<Post> getPostsBySubjectDescending(String subject) {
        return postRepository.findBySubjectOrderByCreatedAtDesc(subject);
    }

    public List<Post> getAllPostsDescending() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Object[]> getPostCountByHour(LocalDateTime startDate, LocalDateTime endDate) {
        return postRepository.countPostsPerHour(startDate, endDate);
    }

    public List<Object[]> getPostCountByDay(LocalDateTime startDate, LocalDateTime endDate) {
        return postRepository.countPostsByDay(startDate, endDate);
    }

    public List<Object[]> getPostCountByMonth(LocalDateTime startDate, LocalDateTime endDate) {
        return postRepository.countPostsByMonth(startDate, endDate);
    }




}
