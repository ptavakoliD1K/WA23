package com.WelfenHub.dto;

import java.sql.Timestamp;

public class MessageDTO {
    private Long id;
    private String content;
    private String username;
    private Long chatRoomId;
    private String chatRoomName;
    private Timestamp createdAt;

    // Default constructor
    public MessageDTO() {}

    // Constructor with all fields
    public MessageDTO(Long id, String content, String username, Long chatRoomId, String chatRoomName, Timestamp createdAt) {
        this.id = id;
        this.content = content;
        this.username = username;
        this.chatRoomId = chatRoomId;
        this.chatRoomName = chatRoomName;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}