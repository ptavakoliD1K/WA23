package com.welfenhub.dto;

public class CommentDTO {
    private Long id;
    private String content;
    private String createdDate;
    private String username;
    private Long postId;

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getUsername() {
        return username;
    }

    public Long getPostId() {
        return postId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
