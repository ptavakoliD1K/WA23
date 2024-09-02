package com.WelfenHub.controllers;

import com.WelfenHub.models.Message;
import com.WelfenHub.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) throws Exception {
        return chatService.sendMessage(message.getChatRoom().getId(), message.getContent(), message.getUser());
    }
}
