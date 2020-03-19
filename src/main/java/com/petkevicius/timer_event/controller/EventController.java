package com.petkevicius.timer_event.controller;

import com.petkevicius.timer_event.exception.DataNotProvidedException;
import com.petkevicius.timer_event.exception.EventNotFoundException;
import com.petkevicius.timer_event.model.Event;
import com.petkevicius.timer_event.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
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
    public ResponseEntity<List<Event>> findAllEvents() {
        try {
            return ResponseEntity.status(200).body(service.findAllEvents());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error!", e);
        }
    }

    @GetMapping(path = "/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Event> findEvent(@PathVariable(name = "id") Optional<String> id) {
        try {
            if (id.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(service.findEvent(id));
            } else {
                throw new DataNotProvidedException();
            }
        } catch (DataNotProvidedException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id not provided!", e);
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found!", e);
        }

    }

    @PostMapping(path = "/events", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) {
        Event savedEvent = service.createEvent(event);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedEvent.getId())
                .toUri();
        System.out.println(location.toASCIIString());
        return ResponseEntity.created(location).body(savedEvent);
    }

    @PutMapping(path = "/events/{id}", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public Event updateEvent(@PathVariable("id") String id, @Valid @RequestBody Event event) {
        try {
            return service.updateEvent(id, event);
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found!", e);
        }
    }

    @DeleteMapping(path = "/events/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable(name = "id") String id) {
        try {
            service.deleteEvent(id);
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found!", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error!", e);
        }
    }


}
