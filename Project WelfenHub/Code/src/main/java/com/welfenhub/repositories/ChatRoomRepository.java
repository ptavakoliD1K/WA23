package com.welfenhub.repositories;

import com.welfenhub.models.ChatRoom;
import com.welfenhub.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}