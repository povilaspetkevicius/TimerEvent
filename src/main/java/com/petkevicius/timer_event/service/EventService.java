package com.petkevicius.timer_event.service;

import com.petkevicius.timer_event.exception.EventNotFoundException;
import com.petkevicius.timer_event.model.Event;
import com.petkevicius.timer_event.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    EventRepository repository;

    @Autowired
    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public Event createOrUpdateEvent(Event event) {
        return repository.save(event);
    }


    public Event findEvent(Optional<String> id) throws EventNotFoundException {
        Optional<Event> event = id.flatMap(s -> repository.findById(s));
        return event.orElseThrow(EventNotFoundException::new);
    }

    public List<Event> findAllEvents() {
        List<Event> events = new ArrayList<Event>();
        Iterable<Event> eventsInRepository = repository.findAll();
        eventsInRepository.forEach(events::add);
        return events;
    }

    public void deleteEvent(String id) {
        repository.deleteById(id);
    }

}
