package com.welfenhub.services;

import com.welfenhub.dto.EventDTO;
import com.welfenhub.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public int getCountOfEvents() {
        return eventRepository.getCount();
    }
}
