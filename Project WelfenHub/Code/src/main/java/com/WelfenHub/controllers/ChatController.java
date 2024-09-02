package com.WelfenHub.controllers;

import com.WelfenHub.models.ChatRoom;
import com.WelfenHub.models.Message;
import com.WelfenHub.models.User;
import com.WelfenHub.services.ChatService;
import com.WelfenHub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @GetMapping("/create")
    public String showCreateChatRoomForm(Model model) {
        model.addAttribute("chatRoom", new ChatRoom());
        List<User> allUsers = userService.findAllUsers();
        model.addAttribute("allUsers", allUsers);
        return "createChatRoom";
    }

    @PostMapping("/group")
    public String createGroupChat(@ModelAttribute ChatRoom chatRoom, Principal principal) {
        logger.info("Create group request received for groupName: {}", chatRoom.getName());
        User creator = userService.findByUsername(principal.getName());
        // Convert usernames to User objects
        List<User> users = userService.findByUsernames(chatRoom.getUsernames());
        users.add(creator);
        chatService.createGroupChat(chatRoom.getName(), users);
        return "redirect:/chat";
    }

    @PostMapping("/private")
    public String createPrivateChat(@RequestParam String username, Principal principal) {
        User user1 = userService.findByUsername(principal.getName());
        User user2 = userService.findByUsername(username);
        chatService.createPrivateChat(user1, user2);
        return "redirect:/chat";
    }

    @PostMapping("/{chatRoomId}/message")
    public String sendMessage(@PathVariable Long chatRoomId, @RequestParam String content, Principal principal) {
        User sender = userService.findByUsername(principal.getName());
        chatService.sendMessage(chatRoomId, content, sender);
        return "redirect:/chat/" + chatRoomId;
    }

    @GetMapping("/{chatRoomId}")
    public String viewChatRoom(@PathVariable Long chatRoomId, Model model, Principal principal) {
        List<Message> messages = chatService.getChatHistory(chatRoomId);
        model.addAttribute("messages", messages);
        return "chat";
    }

    @GetMapping
    public String viewUserChats(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<ChatRoom> chatRooms = chatService.getUserChatRooms(user);
        model.addAttribute("chatRooms", chatRooms);
        return "chatRooms";
    }
}
