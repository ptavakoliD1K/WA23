package com.welfenhub.dto;

import java.sql.Timestamp;

public class MessageDTO {
    private Long id;
    private String content;
    private String username;
    private Long chatRoomId;
    private String chatRoomName;
    private Timestamp createdAt;

    // Neues Feld, um zu markieren, ob die Nachricht "neu" ist
    private boolean isNew;

    // Standardkonstruktor
    public MessageDTO() {}

    // Konstruktor mit allen Feldern
    public MessageDTO(Long id, String content, String username, Long chatRoomId, String chatRoomName, Timestamp createdAt) {
        this.id = id;
        this.content = content;
        this.username = username;
        this.chatRoomId = chatRoomId;
        this.chatRoomName = chatRoomName;
        this.createdAt = createdAt;
    }

    // Getter und Setter
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

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
