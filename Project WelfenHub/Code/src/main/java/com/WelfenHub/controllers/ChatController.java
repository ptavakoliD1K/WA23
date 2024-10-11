package com.WelfenHub.controllers;

import com.WelfenHub.models.ChatRoom;
import com.WelfenHub.models.Message;
import com.WelfenHub.models.User;
import com.WelfenHub.services.ChatService;
import com.WelfenHub.services.UserService;
import com.WelfenHub.dto.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


import java.security.Principal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/create")
    public String showCreateChatRoomForm(Model model) {
        model.addAttribute("chatRoom", new ChatRoom());
        List<User> allUsers = userService.findAllUsers();
        model.addAttribute("allUsers", allUsers);
        return "createChatRoom";
    }

    @GetMapping("/users")
    @ResponseBody
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping("/group")
    public String createGroupChat(@RequestParam String name, @RequestParam List<String> usernames, Principal principal) {
        logger.info("Create group request received for groupName: {}", name);

        // Finde den Ersteller der Gruppe (aktueller Nutzer)
        User creator = userService.findByUsername(principal.getName());

        // Finde die restlichen Benutzer anhand der Usernames
        List<User> users = userService.findByUsernames(usernames);
        users.add(creator);  // Ersteller zur Gruppe hinzufügen

        // Gruppe erstellen
        chatService.createGroupChat(name, users);

        logger.info("Group created successfully with name: {}", name);
        return "redirect:/chat";
    }


    @PostMapping("/private")
    public String createPrivateChat(@RequestParam String username, Principal principal) {
        User user1 = userService.findByUsername(principal.getName());
        User user2 = userService.findByUsername(username);
        chatService.createPrivateChat(user1, user2);
        return "redirect:/chat";
    }

    @GetMapping("/{chatRoomId}/messages")
    @ResponseBody
    public ResponseEntity<List<MessageDTO>> getChatRoomMessages(@PathVariable Long chatRoomId) {
        List<MessageDTO> messages = chatService.getChatHistory(chatRoomId);
        return ResponseEntity.ok(messages);
    }

    @MessageMapping("/chat/{chatRoomId}")
    public void sendMessage(@DestinationVariable Long chatRoomId, MessageDTO messageDTO, Principal principal) {
        User sender = userService.findByUsername(principal.getName());
        ChatRoom chatRoom = chatService.findChatRoomById(chatRoomId);
        MessageDTO savedMessage = chatService.saveMessage(messageDTO, sender, chatRoom);
        messagingTemplate.convertAndSend("/topic/messages/" + chatRoomId, savedMessage);
    }

    @GetMapping
    public String viewUserChats(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<ChatRoom> chatRooms = chatService.getUserChatRooms(user);
        model.addAttribute("chatRooms", chatRooms);
        return "chatRooms";
    }
}