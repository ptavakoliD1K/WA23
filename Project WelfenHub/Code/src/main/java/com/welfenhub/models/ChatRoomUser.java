package com.welfenhub.models;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Join-Entity für ChatRoom und User.
 * Nutzt ChatRoomUserId als EmbeddedId.
 */
@Entity
@Table(name = "chat_room_user")
public class ChatRoomUser {

    @EmbeddedId
    private ChatRoomUserId id;

    @ManyToOne
    @MapsId("chatRoomId")           // referenziert das Feld chatRoomId in ChatRoomUserId
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne
    @MapsId("userId")               // referenziert das Feld userId in ChatRoomUserId
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Zeit, wann der User den Chat zuletzt als gelesen markiert hat.
     */
    private LocalDateTime lastReadAt;

    public ChatRoomUser() {
    }

    /**
     * Bequemer Konstruktor, wenn du ChatRoom und User übergibst.
     */
    public ChatRoomUser(ChatRoom chatRoom, User user) {
        this.chatRoom = chatRoom;
        this.user = user;
        // Erstelle die ID aus den Primärschlüsseln
        this.id = new ChatRoomUserId(
                chatRoom.getId(),
                user.getId()
        );
    }

    // Getter/Setter
    public ChatRoomUserId getId() {
        return id;
    }

    public void setId(ChatRoomUserId id) {
        this.id = id;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getLastReadAt() {
        return lastReadAt;
    }

    public void setLastReadAt(LocalDateTime lastReadAt) {
        this.lastReadAt = lastReadAt;
    }
}
