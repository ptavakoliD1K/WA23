package com.welfenhub.controllers;

import com.welfenhub.services.ChatService;
import com.welfenhub.dto.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import com.welfenhub.models.User;
import com.welfenhub.models.ChatRoom;
import com.welfenhub.services.UserService;
import java.security.Principal;


@Controller
public class WebSocketController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/ws/chat/{chatRoomId}")
    public void handleChatMessage(@DestinationVariable Long chatRoomId, @Payload MessageDTO messageDTO, Principal principal) {
        User sender = userService.findByUsername(principal.getName());
        ChatRoom chatRoom = chatService.findChatRoomById(chatRoomId);
        MessageDTO savedMessage = chatService.saveMessage(messageDTO, sender, chatRoom);
        messagingTemplate.convertAndSend("/topic/messages/" + chatRoomId, savedMessage);
    }
}
