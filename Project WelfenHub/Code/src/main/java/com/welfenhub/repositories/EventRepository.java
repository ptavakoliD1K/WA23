package com.welfenhub.repositories;

import com.welfenhub.dto.EventDTO;
import com.welfenhub.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO events VALUES (null, :title, :content, :date, null)", nativeQuery = true)
    void saveEvent(@Param("title") String title, @Param("content") String content, @Param("date") String date);

    @Query("SELECT new com.welfenhub.dto.EventDTO(e.title, e.content, e.date) FROM Event e")
    List<EventDTO> getEvent();
}
