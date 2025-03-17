package com.welfenhub.services;

import com.welfenhub.dto.MessageDTO;
import com.welfenhub.dto.UnreadNotificationDTO;
import com.welfenhub.models.ChatRoom;
import com.welfenhub.models.ChatRoomUser;
import com.welfenhub.models.Message;
import com.welfenhub.models.User;
import com.welfenhub.repositories.ChatRoomRepository;
import com.welfenhub.repositories.ChatRoomUserRepository;
import com.welfenhub.repositories.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatRoomUserRepository chatRoomUserRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Gruppenchats anlegen – jetzt mit ChatRoomUser.
     */
    @Transactional
    public ChatRoom createGroupChat(String chatRoomName, List<User> users) {
        if (chatRoomName == null || chatRoomName.trim().isEmpty()) {
            throw new IllegalArgumentException("Chat room name cannot be empty");
        }
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("User list cannot be empty");
        }

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(chatRoomName);

        List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

        for (User user : users) {
            ChatRoomUser cru = new ChatRoomUser(chatRoom, user);
            cru.setLastReadAt(LocalDateTime.now());
            chatRoomUsers.add(cru);
        }

        chatRoom.setChatRoomUsers(chatRoomUsers);

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        logger.info("Created group chat room with id: {} and name: {}",
                savedChatRoom.getId(), savedChatRoom.getName());

        // Hier gibst du jetzt den gespeicherten ChatRoom zurück
        return savedChatRoom;
    }


    /**
     * Private Chats anlegen – analog zu createGroupChat().
     */
    @Transactional
    public void createPrivateChat(User user1, User user2) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(user1.getUsername() + " & " + user2.getUsername());

        // Erzeuge ChatRoomUser-Einträge
        List<ChatRoomUser> chatRoomUsers = new ArrayList<>();
        ChatRoomUser cru1 = new ChatRoomUser(chatRoom, user1);
        cru1.setLastReadAt(LocalDateTime.now());
        chatRoomUsers.add(cru1);

        ChatRoomUser cru2 = new ChatRoomUser(chatRoom, user2);
        cru2.setLastReadAt(LocalDateTime.now());
        chatRoomUsers.add(cru2);

        chatRoom.setChatRoomUsers(chatRoomUsers);

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        logger.info("Created private chat room with id: {} between users: {} and {}",
                savedChatRoom.getId(), user1.getUsername(), user2.getUsername());
    }

    /**
     * Nachricht speichern.
     */
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

    /**
     * Chatverlauf laden (alle Nachrichten).
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> getChatHistory(Long chatRoomId) {
        List<Message> messages = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
        return messages.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Chatverlauf laden (paged).
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> getChatHistory(Long chatRoomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        List<Message> messages = messageRepository.findByChatRoomId(chatRoomId, pageable);
        return messages.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Alle Chats des Users holen. Da ChatRoom jetzt keine 'users'-Liste mehr hat,
     * nutzen wir stattdessen ein Repository-Query über ChatRoomUser.
     */
    @Transactional(readOnly = true)
    public List<ChatRoom> getUserChatRooms(User user) {
        // Wir suchen alle Join-Einträge, in denen der user vorkommt:
        List<ChatRoomUser> cruList = chatRoomUserRepository.findByUserId(user.getId());
        // Dann extrahieren wir die ChatRooms und entfernen Duplikate
        List<ChatRoom> chatRooms = cruList.stream()
                .map(ChatRoomUser::getChatRoom)
                .distinct()
                .collect(Collectors.toList());

        logger.info("Found {} chat rooms for user {}", chatRooms.size(), user.getUsername());
        return chatRooms;
    }

    /**
     * Einzelnen Chat suchen
     */
    @Transactional(readOnly = true)
    public ChatRoom findChatRoomById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
    }

    /**
     * Neue Nutzer zu einem bestehenden Chat hinzufügen.
     */
    @Transactional
    public void addUsersToGroup(Long chatRoomId, List<User> users) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid chatRoomId: " + chatRoomId));

        if (chatRoom.getChatRoomUsers() == null) {
            chatRoom.setChatRoomUsers(new ArrayList<>());
        }

        for (User user : users) {
            // Nur hinzufügen, falls nicht schon drin
            boolean alreadyInChat = chatRoom.getChatRoomUsers().stream()
                    .anyMatch(cru -> cru.getUser().getId().equals(user.getId()));
            if (!alreadyInChat) {
                ChatRoomUser cru = new ChatRoomUser(chatRoom, user);
                cru.setLastReadAt(LocalDateTime.now()); // optional
                chatRoom.getChatRoomUsers().add(cru);
            }
        }
        chatRoomRepository.save(chatRoom);
    }

    /**
     * lastReadAt updaten, wenn der User den Chat anklickt.
     */
    @Transactional
    public void updateLastRead(Long chatRoomId, Long userId) {
        ChatRoomUser cru = chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("User not in chatroom"));
        LocalDateTime newTime = LocalDateTime.now();
        logger.info("Updating lastReadAt for chatRoomId {} and userId {}: {} -> {}", chatRoomId, userId, cru.getLastReadAt(), newTime);
        cru.setLastReadAt(newTime);
        chatRoomUserRepository.save(cru);
        chatRoomUserRepository.flush();
    }


    /**
     * Hilfsfunktion: Entity -> DTO
     */
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

    // z.B. in ChatService:
    public List<MessageDTO> getChatHistory(Long chatRoomId, Long userId) {
        List<Message> messages = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);

        // lastReadAt holen:
        ChatRoomUser cru = chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElse(null);
        LocalDateTime lastRead = (cru != null && cru.getLastReadAt() != null)
                ? cru.getLastReadAt()
                : LocalDateTime.of(1970, 1, 1, 0, 0);

        return messages.stream()
                .map(msg -> {
                    MessageDTO dto = convertToDTO(msg);
                    // Vergleichen
                    boolean newer = msg.getCreatedAt().toLocalDateTime().isAfter(lastRead);
                    dto.setNew(newer);  // Dein extra Feld in MessageDTO
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public int countUnreadMessagesForChat(Long chatRoomId, Long userId) {
        ChatRoomUser cru = chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElse(null);
        if (cru == null) {
            return 0; // Falls der User nicht im Chat ist
        }

        // Verwende einen Default-Wert, der innerhalb des zulässigen Bereichs liegt:
        LocalDateTime lastRead = (cru.getLastReadAt() != null) ? cru.getLastReadAt()
                : LocalDateTime.of(1970, 1, 1, 0, 0);

        // Hole den aktuellen Nutzer (das ist in der Join-Entity gespeichert)
        User currentUser = cru.getUser();

        // Zähle Nachrichten, die nach dem lastRead-Zeitpunkt erstellt wurden UND nicht von currentUser sind
        int count = messageRepository.countByChatRoomIdAndCreatedAtAfterAndUserNot(
                chatRoomId,
                Timestamp.valueOf(lastRead),
                currentUser
        );
        return count;
    }

    @Transactional
    public void renameChatRoom(Long chatRoomId, String newName) {
        // ChatRoom laden (oder Exception, wenn nicht vorhanden)
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom mit ID " + chatRoomId + " nicht gefunden."));

        // Neuen Namen setzen und speichern
        chatRoom.setName(newName);
        chatRoomRepository.save(chatRoom);
        logger.info("Chatraum {} wurde umbenannt in '{}'", chatRoomId, newName);
    }

    @Transactional
    public void leaveGroup(Long chatRoomId, Long userId) {
        ChatRoomUser cru = chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User mit ID " + userId + " ist nicht im ChatRoom " + chatRoomId));

        // Den Join-Eintrag aus der Tabelle chat_room_user löschen
        chatRoomUserRepository.delete(cru);

        logger.info("User {} hat den ChatRoom {} verlassen.", userId, chatRoomId);
    }

    @Transactional
    public void handleIncomingMessage(Long chatRoomId, MessageDTO messageDTO, String senderUsername) {
        // 1) Chatroom + Sender laden
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom not found: " + chatRoomId));
        User sender = userService.findByUsername(senderUsername);

        // 2) Nachricht speichern
        MessageDTO savedMessage = saveMessage(messageDTO, sender, chatRoom);

        // 3) An /topic/messages/{chatRoomId} broadcasten
        messagingTemplate.convertAndSend("/topic/messages/" + chatRoomId, savedMessage);

        // 4) Alle Mitglieder (außer Sender) über /user/queue/unread informieren
        //    => Hier kein LazyLoading mehr, weil @Transactional Session offen ist
        List<User> chatMembers = chatRoom.getUsers(); // oder chatRoom.getChatRoomUsers().stream().map(...).toList()
        for (User member : chatMembers) {
            if (!member.getUsername().equals(senderUsername)) {
                int unreadCount = countUnreadMessagesForChat(chatRoomId, member.getId());
                UnreadNotificationDTO unreadDTO = new UnreadNotificationDTO(chatRoomId, unreadCount);

                messagingTemplate.convertAndSendToUser(
                        member.getUsername(),
                        "/queue/unread",
                        unreadDTO
                );
            }
        }
    }



}
