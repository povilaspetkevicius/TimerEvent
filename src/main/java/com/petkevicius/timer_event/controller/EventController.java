package com.petkevicius.timer_event.controller;

import com.petkevicius.timer_event.exception.EventNotFoundException;
import com.petkevicius.timer_event.model.Event;
import com.petkevicius.timer_event.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/")
public class EventController {

    EventService service;

    @Autowired
    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping(path = "/events")
    @ResponseStatus(HttpStatus.OK)
    public List<Event> findEvent(@RequestParam(name = "id") Optional<String> id) throws EventNotFoundException {
        try {
            if (id.isPresent()) {
                return Collections.singletonList(service.findEvent(id));
            } else {
                return service.findAllEvents();
            }
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found", e);
        }

    }

    @PostMapping(path = "/events", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Event createEvent(@Valid @RequestBody Event event) {
        return service.createOrUpdateEvent(event);
    }

    @PutMapping(path = "/events", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public Event updateEvent(@Valid @RequestBody Event event) {
        return service.createOrUpdateEvent(event);
    }

    @DeleteMapping(path = "/events/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable(name = "id") String id) {
        try {
            service.deleteEvent(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception occurred!", e);
        }
    }


}
