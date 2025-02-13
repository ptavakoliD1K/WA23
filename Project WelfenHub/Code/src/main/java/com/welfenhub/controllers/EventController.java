package com.welfenhub.controllers;

import com.welfenhub.dto.EventDTO;
import com.welfenhub.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    EventService eventService;

    /**
     * receives event information from front-end
     *
     * @param event event object, content and title of event
     */

    @PostMapping("/post")
    public void postEvent(@RequestBody EventDTO event) {
        eventService.saveEventToDatabase(event.getTitle(), event.getContent());
    }

    @GetMapping("/get-event")
    public List<EventDTO> getEvent() {
        return eventService.getEventFromDatabase();
    }

    @GetMapping("/get-count")
    public int getCountOfEvents() {
        return eventService.getCountOfEvents();
    }

}
