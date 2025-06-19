package com.ynov.testing.service;

import com.ynov.testing.model.Event;
import com.ynov.testing.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event sampleEvent;

    @BeforeEach
    void setUp() {
        sampleEvent = new Event();
        sampleEvent.setId(1L);
        sampleEvent.setName("Test Event");
        sampleEvent.setDescription("Description");
        sampleEvent.setEventDate(LocalDateTime.of(2025, 1, 1, 12, 0));
        sampleEvent.setActive(true);
    }

    // getAllEvents()
    @Test
    void shouldReturnAllEvents() {
        List<Event> events = Arrays.asList(sampleEvent);
        when(eventRepository.findAll()).thenReturn(events);

        List<Event> result = eventService.getAllEvents();

        assertThat(result).hasSize(1).contains(sampleEvent);
        verify(eventRepository).findAll();
    }

}
