package com.WelfenHub.repositories;

import com.WelfenHub.models.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);

    // New method to support pagination
    List<Message> findByChatRoomId(Long chatRoomId, Pageable pageable);
}