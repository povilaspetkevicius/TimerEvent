package com.petkevicius.timer_event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petkevicius.timer_event.controller.EventController;
import com.petkevicius.timer_event.exception.EventNotFoundException;
import com.petkevicius.timer_event.model.Event;
import com.petkevicius.timer_event.service.EventService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(EventController.class)
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    EventService mockService;

    @Test
    public void testGetAllEvents() throws Exception {
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Event event = getEvent("id", "Event " + i);
            events.add(event);
        }
        given(mockService.findAllEvents()).willReturn(events);

        mockMvc.perform(get("/api/events").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", Matchers.is("Event 0")));
    }

    @Test
    public void getAllEventsAndExceptionIsThrown() throws Exception {

        given(mockService.findAllEvents()).willThrow(RuntimeException.class);

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void getEvent() throws Exception {
        Event event = getEvent("123-abc", "Test event");

        given(mockService.findEvent(Mockito.anyString())).willReturn(event);

        mockMvc.perform(get("/api/events/{id}", "abc-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.name", Matchers.is("Test event")));
    }

    @Test
    public void getEventAndNothingFound() throws Exception {

        given(mockService.findEvent(Mockito.anyString())).willThrow(new EventNotFoundException());

        mockMvc.perform(get("/api/events/{id}", "123-abc"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void createEvent() throws Exception {
        String eventId = "123-abc";
        Event testEvent = getEvent(eventId, "test-event-name");

        given(mockService.createEvent(Mockito.any(Event.class))).willReturn(testEvent);

        mockMvc.perform(post("/api/events")
                .content(asJsonString(testEvent))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", Matchers.containsString(eventId)));
    }

    @Test
    public void createInvalidEventAndGetException() throws Exception {
        Event invalidEvent = getEvent(null, "");

        mockMvc.perform(post("/api/events")
                .content(asJsonString(invalidEvent))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    };

    @Test
    public void updateEvent() throws Exception {
        Event testEvent = getEvent(null,"Test event");

        given(mockService.updateEvent(Mockito.anyString(), Mockito.any(Event.class))).willReturn(testEvent);

        mockMvc.perform(put("/api/events/123").contentType(MediaType.APPLICATION_JSON).content(asJsonString(testEvent)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.name", Matchers.is(testEvent.getName())));
    }

    @Test
    public void updateNonExistingEventAndGetException() throws Exception {
        given(mockService.updateEvent(Mockito.anyString(), Mockito.any(Event.class))).willThrow(EventNotFoundException.class);
        mockMvc.perform(put("/api/events/123").contentType(MediaType.APPLICATION_JSON).content(asJsonString(getEvent(null, "test"))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateWithInvalidEventAndGetException() throws Exception {
        mockMvc.perform(put("/api/events/123").contentType(MediaType.APPLICATION_JSON).content(asJsonString(getEvent(null, ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteEvent() throws Exception {
        String eventID = "test event";
        mockMvc.perform(delete("/api/events/{id}", eventID))
                .andExpect(status().isNoContent());
        verify(mockService, times(1)).deleteEvent(eventID);
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Event getEvent(String eventId, String s) throws Exception {

        Event testEvent = new Event();
        FieldSetter.setField(testEvent, testEvent.getClass().getDeclaredField("id"), eventId);
        testEvent.setName(s);
        return testEvent;
    }
}
