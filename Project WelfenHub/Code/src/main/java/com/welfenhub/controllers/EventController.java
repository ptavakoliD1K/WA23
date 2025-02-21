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

    /**
     * gets events from database
     *
     * @return
     */

    @GetMapping("/get-event")
    public List<EventDTO> getEvent() {
        return eventService.getEventFromDatabase();
    }

    /**
     * gets number of events
     *
     * @return
     */

    @GetMapping("/get-event-count")
    public int getEventCount() {
        return eventService.getEventCount();
    }

    /**
     * gets events for each page
     *
     * @param page
     * @return
     */

    @GetMapping("/show")
    public List<EventDTO> showEventByPage(@RequestParam("page") int page) {
        return eventService.showEventsByPage(page);
    }

    /**
     * deletes event in database
     *
     * @param event
     */

    @DeleteMapping("/remove")
    public void removeEvent(@RequestBody EventDTO event) {

        eventService.removeEventFromDatabase(event.getContent(), event.getTitle(), event.getDate());
    }

}
