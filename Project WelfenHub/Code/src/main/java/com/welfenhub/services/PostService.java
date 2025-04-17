package com.welfenhub.services;

import com.welfenhub.models.Post;
import com.welfenhub.models.User;
import com.welfenhub.models.Comment;
import com.welfenhub.repositories.UserRepository;
import com.welfenhub.repositories.PostRepository;
import com.welfenhub.repositories.CommentRepository;
import com.welfenhub.models.Reaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.ArrayList;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName());
    }

    public List<Post> getPostsByCourse(String course) {
        User currentUser = getCurrentUser();
        List<Post> posts = postRepository.findByCourse(course);

        for (Post post : posts) {
            List<Comment> sorted = new ArrayList<>(post.getComments());
            sorted.sort(Comparator.comparing(Comment::getCreatedDate).reversed());
            post.setSortedComments(sorted);
            post.setLikedByUser(post.getReactions().stream()
                    .anyMatch(reaction -> reaction.getUser().getId().equals(currentUser.getId())));
        }

        return posts;
    }

    public List<Post> getPostsBySubjectDescending(String subject) {
        User currentUser = getCurrentUser();
        List<Post> posts = postRepository.findBySubjectOrderByCreatedAtDesc(subject);
        for (Post post : posts) {
            post.setLikedByUser(post.getReactions().stream()
                    .anyMatch(r -> r.getUser().getId().equals(currentUser.getId())));
        }
        return posts;
    }

    public List<Post> getAllPosts() {
        User currentUser = getCurrentUser();
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        for (Post post : posts) {
            post.setLikedByUser(post.getReactions().stream()
                    .anyMatch(r -> r.getUser().getId().equals(currentUser.getId())));
        }
        return posts;
    }

    public long getTotalPostCount() {
        return postRepository.count();
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post nicht gefunden"));
    }

    public Post createPost(String title, String content, String course, int semester, String subject, User user) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCourse(course);
        post.setSemester(semester);
        post.setSubject(subject);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void save(Post post) {
        postRepository.save(post);
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    public List<Post> searchPostsByTitle(String query) {
        User currentUser = getCurrentUser();
        List<Post> posts = postRepository.findByTitleContainingIgnoreCase(query);
        for (Post post : posts) {
            post.setLikedByUser(post.getReactions().stream()
                    .anyMatch(r -> r.getUser().getId().equals(currentUser.getId())));
        }
        return posts;
    }

    public Post getLatestPostForSubject(String subject) {
        return postRepository.findTopByCourseOrderByCreatedAtDesc(subject);
    }

    public List<Object[]> getPostCountByHour(LocalDateTime start, LocalDateTime end) {
        return postRepository.countPostsPerHour(start, end);
    }

    public List<Object[]> getPostCountByDay(LocalDateTime start, LocalDateTime end) {
        return postRepository.countPostsByDay(start, end);
    }

    public List<Object[]> getPostCountByMonth(LocalDateTime start, LocalDateTime end) {
        return postRepository.countPostsByMonth(start, end);
    }

    public Comment addComment(Long postId, String content, String username) {
        Post post = findById(postId);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User nicht gefunden");
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);
        comment.setCreatedDate(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    public Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Kommentar nicht gefunden"));
    }

    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public int toggleReaction(Long postId, User user) {
        Post post = findById(postId);

        Reaction existing = post.getReactions().stream()
                .filter(r -> r.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            post.getReactions().remove(existing);
        } else {
            Reaction newReaction = new Reaction();
            newReaction.setPost(post);
            newReaction.setUser(user);
            post.getReactions().add(newReaction);
        }

        save(post);
        return post.getReactions().size();
    }

    public List<Post> getPostsByCourseSortedByLikes(String course) {
        User currentUser = getCurrentUser();
        List<Post> posts = postRepository.findByCourse(course);

        for (Post post : posts) {
            List<Comment> sorted = new ArrayList<>(post.getComments());
            sorted.sort(Comparator.comparing(Comment::getCreatedDate).reversed());
            post.setSortedComments(sorted);
            post.setLikedByUser(post.getReactions().stream()
                    .anyMatch(r -> r.getUser().getId().equals(currentUser.getId())));
        }

        posts.sort(Comparator.comparingInt((Post p) -> p.getReactions().size()).reversed());
        return posts;
    }

    public List<Post> getPostsByCourseSortedByDate(String course) {
        User currentUser = getCurrentUser();
        List<Post> posts = postRepository.findByCourse(course);
        posts.sort(Comparator.comparing(Post::getCreatedAt).reversed());

        for (Post post : posts) {
            List<Comment> sorted = new ArrayList<>(post.getComments());
            sorted.sort(Comparator.comparing(Comment::getCreatedDate).reversed());
            post.setSortedComments(sorted);
            post.setLikedByUser(post.getReactions().stream()
                    .anyMatch(r -> r.getUser().getId().equals(currentUser.getId())));
        }

        return posts;
    }
}