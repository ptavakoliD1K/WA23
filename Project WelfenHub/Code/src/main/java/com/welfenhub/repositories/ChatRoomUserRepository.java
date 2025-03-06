package com.welfenhub.repositories;

import com.welfenhub.models.ChatRoomUser;
import com.welfenhub.models.ChatRoomUserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, ChatRoomUserId> {

    Optional<ChatRoomUser> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);

    // z.B. alle ChatRoomUser eines Users
    List<ChatRoomUser> findByUserId(Long userId);

    // Oder alle zu einem ChatRoom
    List<ChatRoomUser> findByChatRoomId(Long chatRoomId);
}
