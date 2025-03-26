package com.welfenhub.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;



@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 5000)
    private String content;

    // Neues Feld für den Kurs
    @Column(nullable = false)
    private String course;

    // Neues Feld für das Semester
    @Column(nullable = false)
    private int semester;

    @Column(nullable = false)
    private String subject;

    private int reactions = 0;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments;

    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(name = "post_reactions",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> reactedUsers = new HashSet<>();

    public boolean toggleReaction(User user) {
        if (reactedUsers.contains(user)) {
            reactedUsers.remove(user);
            return false; // Reaktion entfernt
        } else {
            reactedUsers.add(user);
            return true;  // Reaktion hinzugefügt
        }
    }

    public int getReactionCount() {
        return reactedUsers.size();
    }

    // Getter und Setter
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getReactions() {
        return reactions;
    }

    public void setReactions(int reactions) {
        this.reactions = reactions;
    }

    public void addReaction() {
        this.reactions++;
    }

    public Set<User> getReactedUsers() {
        return reactedUsers;
    }

}

