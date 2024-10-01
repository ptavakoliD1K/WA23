package com.WelfenHub.services;

import com.WelfenHub.models.ChatRoom;
import com.WelfenHub.models.Message;
import com.WelfenHub.models.User;
import com.WelfenHub.repositories.ChatRoomRepository;
import com.WelfenHub.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    public void createGroupChat(String chatRoomName, List<User> users) {
        if (chatRoomName == null || chatRoomName.trim().isEmpty()) {
            throw new IllegalArgumentException("Chat room name cannot be empty");
        }
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("User list cannot be empty");
        }
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(chatRoomName);
        chatRoom.setUsers(users);
        chatRoomRepository.save(chatRoom);
    }

    public void createPrivateChat(User user1, User user2) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setUsers(List.of(user1, user2));
        chatRoom.setName(user1.getUsername() + " & " + user2.getUsername());
        chatRoomRepository.save(chatRoom);
    }

    public Message saveMessage(Message message) {
        if (message.getChatRoom() == null || !chatRoomRepository.existsById(message.getChatRoom().getId())) {
            throw new IllegalArgumentException("Invalid chat room");
        }
        return messageRepository.save(message);
    }

    public List<Message> getChatHistory(Long chatRoomId) {
        return messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
    }

    public List<Message> getChatHistory(Long chatRoomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        return messageRepository.findByChatRoomId(chatRoomId, pageable);
    }

    public List<ChatRoom> getUserChatRooms(User user) {
        return chatRoomRepository.findByUsersContaining(user);
    }

    public ChatRoom findChatRoomById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
    }

    public boolean isUserInChatRoom(Long chatRoomId, User user) {
        return chatRoomRepository.existsByIdAndUsersContaining(chatRoomId, user);
    }

    public void addUserToChatRoom(Long chatRoomId, User user) {
        ChatRoom chatRoom = findChatRoomById(chatRoomId);
        if (!chatRoom.getUsers().contains(user)) {
            chatRoom.getUsers().add(user);
            chatRoomRepository.save(chatRoom);
        }
    }

    public void removeUserFromChatRoom(Long chatRoomId, User user) {
        ChatRoom chatRoom = findChatRoomById(chatRoomId);
        if (chatRoom.getUsers().remove(user)) {
            chatRoomRepository.save(chatRoom);
        }
    }

    public Message sendMessage(Long chatRoomId, String content, User sender) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        Message message = new Message();
        message.setContent(content);
        message.setUser(sender);
        message.setChatRoom(chatRoom);
        message.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        return messageRepository.save(message);
    }
}