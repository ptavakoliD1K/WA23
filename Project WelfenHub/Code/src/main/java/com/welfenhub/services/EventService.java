package com.welfenhub.services;

import com.welfenhub.dto.EventDTO;
import com.welfenhub.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;

    /**
     * saves event title, content and date to database
     *
     * @param title   title of event
     * @param content text of event
     */

    public void saveEventToDatabase(String title, String content) {

        LocalDateTime creationDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        String date = formatter.format(creationDate);

        eventRepository.saveEvent(title, content, date);
    }

    /**
     * gets EventDTO from database
     */

    public List<EventDTO> getEventFromDatabase() {

        return eventRepository.getEvent();
    }

    /**
     * gets number of events
     *
     * @return
     */

    public int getEventCount() {
        return eventRepository.getEventCount();
    }

    /**
     * gets events for each page
     *
     * @param page
     * @return
     */

    public List<EventDTO> showEventsByPage(int page) {
        Pageable pageable = PageRequest.of(page - 1, 5, Sort.by(Sort.Direction.DESC, "id"));

        return eventRepository.showEvent(pageable);
    }

    /**
     * removes event in database
     *
     * @param content
     * @param title
     * @param date
     */

    public void removeEventFromDatabase(String content, String title, String date) {
        eventRepository.removeEvent(title, content, date);
    }
}
