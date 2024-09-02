package com.WelfenHub.services;

import com.WelfenHub.models.ChatRoom;
import com.WelfenHub.models.Message;
import com.WelfenHub.models.User;
import com.WelfenHub.repositories.ChatRoomRepository;
import com.WelfenHub.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    public void createGroupChat(String chatRoomName, List<User> users) {
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

    public Message sendMessage(Long chatRoomId, String content, User sender) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatRoomId);
        if (chatRoomOptional.isPresent()) {
            ChatRoom chatRoom = chatRoomOptional.get();
            Message message = new Message();
            message.setChatRoom(chatRoom);
            message.setUser(sender);
            message.setContent(content);
            message.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            return messageRepository.save(message);
        } else {
            throw new IllegalArgumentException("Chat room not found");
        }
    }

    public List<Message> getChatHistory(Long chatRoomId) {
        return messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
    }

    public List<ChatRoom> getUserChatRooms(User user) {
        return chatRoomRepository.findByUsersContaining(user);
    }
}
