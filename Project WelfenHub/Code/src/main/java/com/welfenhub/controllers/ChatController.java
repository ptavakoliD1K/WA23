package com.welfenhub.controllers;

import com.welfenhub.models.ChatRoom;
import com.welfenhub.models.User;
import com.welfenhub.services.ChatService;
import com.welfenhub.services.UserService;
import com.welfenhub.dto.MessageDTO;
import com.welfenhub.dto.UserDTO;
import com.welfenhub.dto.ChatRoomDTO;
import com.welfenhub.dto.RenameRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.welfenhub.models.ChatRoomUser;
import java.util.stream.Collectors;
import com.welfenhub.dto.UnreadNotificationDTO;



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

        User creator = userService.findByUsername(principal.getName());
        List<User> users = userService.findByUsernames(usernames);
        users.add(creator);

        // 🚀 Gruppe erstellen und speichern
        ChatRoom createdRoom = chatService.createGroupChat(name, users);

        // 🔥 WebSocket-Nachricht an alle verbundenen Nutzer senden
        messagingTemplate.convertAndSend(
                "/topic/new-group",
                new ChatRoomDTO(createdRoom.getId(), createdRoom.getName())
        );

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
    public void sendMessage(@DestinationVariable Long chatRoomId,
                            MessageDTO messageDTO,
                            Principal principal) {
        // Hier keine Logik mehr, nur an die Service-Methode delegieren
        String senderUsername = principal.getName();
        chatService.handleIncomingMessage(chatRoomId, messageDTO, senderUsername);
    }



    @GetMapping("")
    public String viewUserChats(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<ChatRoom> chatRooms = chatService.getUserChatRooms(user);

        // Berechne für jeden Chatroom den unreadCount (z. B. on the fly)
        for (ChatRoom room : chatRooms) {
            int count = chatService.countUnreadMessagesForChat(room.getId(), user.getId());
            room.setUnreadCount(count);
        }
        model.addAttribute("chatRooms", chatRooms);
        return "ChatRooms";
    }

    @PostMapping("/{chatRoomId}/addUsers")
    public String addUsersToGroup(@PathVariable Long chatRoomId, @RequestBody Map<String, Object> userData, Principal principal) {
        logger.info("Add users request received for chatRoomId: {}", chatRoomId);

        List<String> usernames = (List<String>) userData.get("usernames");

        // Finde die Benutzer anhand der Usernames
        List<User> users = userService.findByUsernames(usernames);

        // Benutzer zur Gruppe hinzufügen
        chatService.addUsersToGroup(chatRoomId, users);

        logger.info("Users added successfully to chatRoomId: {}", chatRoomId);
        return "redirect:/chat";
    }

    @PostMapping("/{chatRoomId}/markAsRead")
    @ResponseBody
    public void markAsRead(@PathVariable Long chatRoomId, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        chatService.updateLastRead(chatRoomId, user.getId());
        // Kein Redirect, wir geben nur Status 200 zurück
    }

    @GetMapping("/{chatRoomId}/members")
    public String viewGroupMembers(@PathVariable Long chatRoomId, Model model) {
        ChatRoom chatRoom = chatService.findChatRoomById(chatRoomId);
        // Extrahiere die User aus den ChatRoomUser-Objekten
        List<User> members = chatRoom.getChatRoomUsers().stream()
                .map(ChatRoomUser::getUser)
                .distinct()
                .collect(Collectors.toList());
        model.addAttribute("chatRoom", chatRoom);
        model.addAttribute("members", members);
        return "groupMembers";  // Name des Thymeleaf-Templates
    }

    @GetMapping("/{chatRoomId}/members/json")
    @ResponseBody
    public List<User> getMembers(@PathVariable Long chatRoomId) {
        ChatRoom chatRoom = chatService.findChatRoomById(chatRoomId);
        // Extrahiere die User aus den ChatRoomUser-Einträgen
        List<User> members = chatRoom.getChatRoomUsers().stream()
                .map(ChatRoomUser::getUser)
                .distinct()
                .collect(Collectors.toList());
        return members;
    }

    @GetMapping("/{chatRoomId}/non-members")
    @ResponseBody
    public List<UserDTO> getNonMembers(@PathVariable Long chatRoomId, Principal principal) {
        ChatRoom chatRoom = chatService.findChatRoomById(chatRoomId);
        List<User> allUsers = userService.findAllUsers();
        List<User> members = chatRoom.getChatRoomUsers().stream()
                .map(ChatRoomUser::getUser)
                .collect(Collectors.toList());

        // Filtere Mitglieder heraus, die bereits im Raum sind
        return userService.findAllUsers().stream()
                .filter(user -> !members.contains(user))
                .filter(user -> !user.getUsername().equals(principal.getName()))
                .map(user -> new UserDTO(user.getUsername(), user.getFullName()))
                .collect(Collectors.toList());
    }

    @PostMapping("/{chatRoomId}/rename")
    public String renameChatRoom(
            @PathVariable Long chatRoomId,
            @RequestBody Map<String, Object> renameData) {

        String newName = (String) renameData.get("newName");
        if (newName == null || newName.trim().isEmpty()) {
            return "redirect:/chat"; // o.ä.
        }

        try {
            chatService.renameChatRoom(chatRoomId, newName);
            return "redirect:/chat";
        } catch (IllegalArgumentException e) {
            // Chat nicht gefunden -> redirect z.B. zu /chat mit Fehler
            return "redirect:/chat";
        }
    }

    @PostMapping("/{chatRoomId}/leave")
    public String leaveGroup(
            @PathVariable Long chatRoomId,
            Principal principal
    ) {
        try {
            // Gerade eingeloggter User
            User user = userService.findByUsername(principal.getName());
            // Service aufrufen
            chatService.leaveGroup(chatRoomId, user.getId());

            // Erfolg: Zurück zur Übersichtsseite
            return "redirect:/chat";
        } catch (IllegalArgumentException e) {
            // Chat nicht gefunden, oder User ist nicht im Chat
            return "redirect:/chat";
        } catch (Exception e) {
            // Sonstige Probleme
            return "redirect:/chat";
        }
    }








}