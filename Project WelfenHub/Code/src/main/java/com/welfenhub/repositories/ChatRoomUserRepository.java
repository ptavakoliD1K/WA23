package com.welfenhub.repositories;

import com.welfenhub.models.ChatRoomUser;
import com.welfenhub.models.ChatRoomUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, ChatRoomUserId> {

    Optional<ChatRoomUser> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);

    // z.B. alle ChatRoomUser eines Users
    List<ChatRoomUser> findByUserId(Long userId);

    // Oder alle zu einem ChatRoom
    List<ChatRoomUser> findByChatRoomId(Long chatRoomId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ChatRoomUser cru WHERE cru.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
