package com.WelfenHub.services;

import com.WelfenHub.models.Post;
import com.WelfenHub.models.User;
import com.WelfenHub.models.Comment;
import com.WelfenHub.repositories.PostRepository;
import com.WelfenHub.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    public List<Post> getPostsByCourse(String course) {
        return postRepository.findByCourse(course);
    }

    public Post createPost(String title, String content, String course, int semester, String subject, User user) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCourse(course);
        post.setSemester(semester);
        post.setSubject(subject); // subject hinzufügen
        post.setUser(user);
        post.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return postRepository.save(post);
    }


    public void addComment(Long postId, String content, User user) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setPost(post);
            comment.setUser(user);
            commentRepository.save(comment);
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

}
