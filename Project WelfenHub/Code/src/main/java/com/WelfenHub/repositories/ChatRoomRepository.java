package com.WelfenHub.repositories;

import com.WelfenHub.models.ChatRoom;
import com.WelfenHub.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByUsersContaining(User user);
}
