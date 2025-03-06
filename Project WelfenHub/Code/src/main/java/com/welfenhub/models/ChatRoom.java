package com.welfenhub.models;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomUser> chatRoomUsers;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;

    // Falls du ein Transient-Feld brauchst, z.B. userNames:
    @Transient
    private List<String> usernames;

    @Transient
    private int unreadCount;

    public List<User> getUsers() {
        if(chatRoomUsers == null) {
            return new ArrayList<>();
        }
        return chatRoomUsers.stream()
                .map(ChatRoomUser::getUser)
                .distinct()
                .collect(Collectors.toList());
    }
    // Getter und Setter
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ChatRoomUser> getChatRoomUsers() {
        return chatRoomUsers;
    }

    public void setChatRoomUsers(List<ChatRoomUser> chatRoomUsers) {
        this.chatRoomUsers = chatRoomUsers;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public int getUnreadCount() {
        return unreadCount;
    }
    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
