package com.WelfenHub.services;

import com.WelfenHub.models.ChatRoom;
import com.WelfenHub.models.Message;
import com.WelfenHub.models.User;
import com.WelfenHub.repositories.ChatRoomRepository;
import com.WelfenHub.repositories.MessageRepository;
import com.WelfenHub.dto.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Transactional
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
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        logger.info("Created chat room with id: {} and name: {}", savedChatRoom.getId(), savedChatRoom.getName());
    }

    @Transactional
    public void createPrivateChat(User user1, User user2) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setUsers(List.of(user1, user2));
        chatRoom.setName(user1.getUsername() + " & " + user2.getUsername());
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        logger.info("Created private chat room with id: {} between users: {} and {}", savedChatRoom.getId(), user1.getUsername(), user2.getUsername());
    }

    @Transactional
    public MessageDTO saveMessage(MessageDTO messageDTO, User sender, ChatRoom chatRoom) {
        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setUser(sender);
        message.setChatRoom(chatRoom);
        message.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        Message savedMessage = messageRepository.save(message);
        return convertToDTO(savedMessage);
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getChatHistory(Long chatRoomId) {
        List<Message> messages = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
        return messages.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getChatHistory(Long chatRoomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        List<Message> messages = messageRepository.findByChatRoomId(chatRoomId, pageable);
        return messages.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getUserChatRooms(User user) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByUsersContaining(user);
        logger.info("Found {} chat rooms for user {}", chatRooms.size(), user.getUsername());
        return chatRooms;
    }

    @Transactional(readOnly = true)
    public ChatRoom findChatRoomById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
    }

    private MessageDTO convertToDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getContent(),
                message.getUser().getUsername(),
                message.getChatRoom().getId(),
                message.getChatRoom().getName(),
                message.getCreatedAt()
        );
    }

    public void addUsersToGroup(Long chatRoomId, List<User> users) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid chatRoomId: " + chatRoomId));

        chatRoom.getUsers().addAll(users);
        chatRoomRepository.save(chatRoom);
    }
}