package com.welfenhub.controllers;

import com.welfenhub.dto.MessageDTO;
import com.welfenhub.models.*;
import com.welfenhub.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import java.util.List;


import java.security.Principal;
import java.util.Map;

@Controller
public class WebSocketController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;


    // Existierende Methode für Chats
    @MessageMapping("/ws/chat/{chatRoomId}")
    public void handleChatMessage(@DestinationVariable Long chatRoomId, @Payload MessageDTO messageDTO, Principal principal) {
        User sender = userService.findByUsername(principal.getName());
        ChatRoom chatRoom = chatService.findChatRoomById(chatRoomId);
        MessageDTO savedMessage = chatService.saveMessage(messageDTO, sender, chatRoom);
        messagingTemplate.convertAndSend("/topic/messages/" + chatRoomId, savedMessage);
    }

    @MessageMapping("/newPost")
    public void handleNewPost(@Payload Map<String, String> payload, Principal principal) {
        String title = payload.get("title");
        String content = payload.get("content");
        String course = payload.get("course");
        String subject = payload.get("subject");
        int semester = Integer.parseInt(payload.get("semester"));

        User user = userService.findByUsername(principal.getName());
        Post createdPost = postService.createPost(title, content, course, semester, subject, user);

        createdPost.setReactions(List.of());

        messagingTemplate.convertAndSend("/topic/posts", createdPost);
    }


    // NEUE Methode für Kommentare
    @MessageMapping("/newComment")
    public void handleNewComment(@Payload Map<String, String> payload, Principal principal) {
        Long postId = Long.parseLong(payload.get("postId"));
        String content = payload.get("content");

        User user = userService.findByUsername(principal.getName());
        Comment createdComment = postService.addComment(postId, content, user.getUsername());

        messagingTemplate.convertAndSend("/topic/comments", createdComment);
    }

    @MessageMapping("/reactToPost")
    public void handleReaction(@Payload Map<String, String> payload, Principal principal) {
        Long postId = Long.parseLong(payload.get("postId"));
        User user = userService.findByUsername(principal.getName());

        int updatedCount = postService.toggleReaction(postId, user);

        Map<String, Object> updatePayload = Map.of(
                "postId", postId,
                "reactionCount", updatedCount
        );

        messagingTemplate.convertAndSend("/topic/reactions", updatePayload);
    }




}
