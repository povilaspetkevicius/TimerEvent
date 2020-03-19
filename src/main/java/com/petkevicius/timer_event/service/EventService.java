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

    public Event createEvent(Event event) {
        return repository.save(event);
    }

    public Event updateEvent(String id, Event event) throws EventNotFoundException {
        Optional<Event> existingEvent = repository.findById(id);
        if (existingEvent.isPresent()){
            Event mergedEvent = existingEvent.get();
            mergedEvent.setName(event.getName());
            return repository.save(mergedEvent);
        } else {
            throw new EventNotFoundException();
        }
    }


    public Event findEvent(String id) throws EventNotFoundException {
        Optional<Event> event = repository.findById(id);
        return event.orElseThrow(EventNotFoundException::new);
    }

    public List<Event> findAllEvents() {
        List<Event> events = new ArrayList<Event>();
        Iterable<Event> eventsInRepository = repository.findAll();
        eventsInRepository.forEach(events::add);
        return events;
    }

    public void deleteEvent(String id) throws EventNotFoundException {
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new EventNotFoundException();
        }
    }

}
