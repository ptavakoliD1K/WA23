package com.welfenhub.repositories;

import com.welfenhub.models.ChatRoom;
import com.welfenhub.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByUsersContaining(User user);

    // New method to check if a user is in a chat room
    boolean existsByIdAndUsersContaining(Long id, User user);
}