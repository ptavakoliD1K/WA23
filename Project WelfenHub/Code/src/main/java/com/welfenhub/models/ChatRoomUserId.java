package com.welfenhub.models;

import javax.persistence.Embeddable;
import javax.persistence.Column;
import java.io.Serializable;
import java.util.Objects;

/**
 * Embeddable ID für die Join-Entity
 * (bildet den Composite Key aus chatRoomId & userId)
 */
@Embeddable
public class ChatRoomUserId implements Serializable {

    @Column(name = "chat_room_id")
    private Long chatRoomId;

    @Column(name = "user_id")
    private Long userId;

    public ChatRoomUserId() {}

    public ChatRoomUserId(Long chatRoomId, Long userId) {
        this.chatRoomId = chatRoomId;
        this.userId = userId;
    }

    public Long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // equals & hashCode nach chatRoomId + userId
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatRoomUserId)) return false;
        ChatRoomUserId that = (ChatRoomUserId) o;
        return Objects.equals(chatRoomId, that.chatRoomId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatRoomId, userId);
    }
}
