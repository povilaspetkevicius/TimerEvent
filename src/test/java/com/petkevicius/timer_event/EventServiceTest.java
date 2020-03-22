package com.petkevicius.timer_event;

import com.petkevicius.timer_event.exception.EventNotFoundException;
import com.petkevicius.timer_event.model.Event;
import com.petkevicius.timer_event.repository.EventRepository;
import com.petkevicius.timer_event.service.EventService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class EventServiceTest {

    @MockBean
    EventRepository repositoryMock;

    @Autowired
    EventService service;

    @Test
    public void createEvent() throws Exception {

        Event event = createMockEvent();
        ArgumentCaptor<Event> argument = ArgumentCaptor.forClass(Event.class);

        service.createEvent(event);

        verify(repositoryMock).save(argument.capture());

        Assertions.assertEquals(argument.getValue(), event);
    }

    @Test
    public void updateEvent() throws Exception {
        Event existingEvent = createMockEvent();
        existingEvent.setName("existing-name");
        Event newEvent = createMockEvent();
        ArgumentCaptor<Event> argument = ArgumentCaptor.forClass(Event.class);
        given(repositoryMock.findById(Mockito.anyString())).willReturn(Optional.of(existingEvent));
        service.updateEvent("test-id", newEvent);
        verify(repositoryMock).save(argument.capture());
        Assertions.assertEquals(argument.getValue().hashCode(), existingEvent.hashCode());
        Assertions.assertEquals(existingEvent.getName(), newEvent.getName());

    }

    @Test
    public void updateEventShouldThrowExceptionIfEventNotFound() {
        given(repositoryMock.findById(Mockito.anyString())).willReturn(Optional.empty());
        Assertions.assertThrows(EventNotFoundException.class
                , () -> {
                    service.updateEvent("test-id", createMockEvent());
                }
                , "Expected exception to be thrown."
        );
    }

    @Test
    public void findEvent() throws Exception {
        given(repositoryMock.findById(Mockito.anyString())).willReturn(Optional.of(createMockEvent()));

        Event event = service.findEvent("test-id");

        Assertions.assertNotNull(event);
    }

    @Test
    public void findEventShouldThrowExceptionIfEventNotFound() {
        given(repositoryMock.findById(Mockito.anyString())).willReturn(Optional.empty());
        Assertions.assertThrows(EventNotFoundException.class
                , () -> {
                    service.findEvent("test-id");
                }
                , "Expected exception to be thrown."
        );
    }

    @Test
    public void findAllEvents() throws Exception {
        Iterable<Event> events = Arrays.asList( createMockEvent() );
        given(repositoryMock.findAll()).willReturn(events);
        Object o = service.findAllEvents();
        Assertions.assertNotNull(o);
    }

    @Test
    public void findAllEventsReturnsListObject() throws Exception {
        Iterable<Event> events = Collections.singletonList(createMockEvent());
        given(repositoryMock.findAll()).willReturn(events);
        Object o = service.findAllEvents();
        Assertions.assertTrue(o instanceof ArrayList);
    }

    @Test
    public void deleteEvent() throws Exception {
        ArgumentCaptor<String> argumentForMethod = ArgumentCaptor.forClass(String.class);
        String deletableId = "test-id";
        service.deleteEvent(deletableId);
        verify(repositoryMock).deleteById(argumentForMethod.capture());
        Assertions.assertEquals(argumentForMethod.getValue(), deletableId);
    }


    public Event createMockEvent() throws NoSuchFieldException {
        Event testEvent = new Event();
        FieldSetter.setField(testEvent, testEvent.getClass().getDeclaredField("id"), "test-id");
        testEvent.setName("test-event");
        return testEvent;
    }
}
