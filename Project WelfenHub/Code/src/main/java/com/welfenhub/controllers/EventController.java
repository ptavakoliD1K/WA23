package com.welfenhub.controllers;

import com.welfenhub.dto.EventDTO;
import com.welfenhub.services.EventService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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


    @DeleteMapping("/remove")
    public void removeEvent(@RequestBody EventDTO event) {
        System.out.println(event.getContent());
        System.out.println(event.getTitle());
        System.out.println(event.getDate());

        // TODO: Parameter weitergeben, über DB eintrag löschen

        eventService.removeEventFromDatabase();
    }
}
