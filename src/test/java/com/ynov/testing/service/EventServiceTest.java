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
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Event Service Unit Tests")
public class EventServiceTest {
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

    @Test
    void shouldReturnAllEvents() {
        List<Event> events = Arrays.asList(sampleEvent);
        when(eventRepository.findAll()).thenReturn(events);

        List<Event> result = eventService.getAllEvents();

        assertThat(result).hasSize(1).contains(sampleEvent);
        verify(eventRepository).findAll();
    }

    @Test
    void shouldReturnEventWhenIdValid() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));

        Optional<Event> result = eventService.getEventById(1L);

        assertThat(result).isPresent().get().isEqualTo(sampleEvent);
    }

    @Test
    void shouldThrowWhenGetEventByIdWithNullOrNonPositive() {
        assertThatThrownBy(() -> eventService.getEventById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event ID must be positive");

        assertThatThrownBy(() -> eventService.getEventById(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event ID must be positive");
    }

    @Test
    void shouldReturnEmptyWhenEventNotFound() {
        when(eventRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Event> result = eventService.getEventById(2L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldCreateEventWhenValid() {
        when(eventRepository.existsByName("Test Event")).thenReturn(false);
        when(eventRepository.save(sampleEvent)).thenReturn(sampleEvent);

        Event result = eventService.createEvent(sampleEvent);

        assertThat(result).isEqualTo(sampleEvent);
        verify(eventRepository).save(sampleEvent);
    }

    @Test
    void shouldThrowWhenCreateEventNameExists() {
        when(eventRepository.existsByName("Test Event")).thenReturn(true);

        assertThatThrownBy(() -> eventService.createEvent(sampleEvent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Event name already exists: Test Event");
    }

    @Test
    void shouldValidateCreateEventFields() {

        assertThatThrownBy(() -> eventService.createEvent(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event cannot be null");

        Event e1 = new Event();
        e1.setDescription("Desc");
        e1.setEventDate(LocalDateTime.now());
        assertThatThrownBy(() -> eventService.createEvent(e1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event name is required");

        Event e2 = new Event();
        e2.setName("Name");
        e2.setEventDate(LocalDateTime.now());
        assertThatThrownBy(() -> eventService.createEvent(e2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event description is required");

        Event e3 = new Event();
        e3.setName("Name");
        e3.setDescription("Desc");
        assertThatThrownBy(() -> eventService.createEvent(e3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event date is required");
    }

    @Test
    void shouldUpdateEventWhenExistsAndValid() {
        Event updated = new Event();
        updated.setName("Updated");
        updated.setDescription("New Desc");
        updated.setEventDate(LocalDateTime.of(2025, 2, 2, 14, 0));
        updated.setActive(false);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        Event result = eventService.updateEvent(1L, updated);

        assertThat(result.getName()).isEqualTo("Updated");
        assertThat(result.getDescription()).isEqualTo("New Desc");
        assertThat(result.getEventDate()).isEqualTo(LocalDateTime.of(2025, 2, 2, 14, 0));
        assertThat(result.getActive()).isFalse();
    }

    @Test
    void shouldThrowWhenUpdateEventNotFound() {
        when(eventRepository.findById(2L)).thenReturn(Optional.empty());

        Event data = new Event();
        data.setName("N"); data.setDescription("D"); data.setEventDate(LocalDateTime.now());

        assertThatThrownBy(() -> eventService.updateEvent(2L, data))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event not found with ID: 2");
    }

    @Test
    void shouldValidateUpdateEventData() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));

        Event invalid = new Event();
        invalid.setName(" ");
        invalid.setDescription("D");
        invalid.setEventDate(LocalDateTime.now());
        assertThatThrownBy(() -> eventService.updateEvent(1L, invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event name is required");
    }

    @Test
    void shouldDeleteWhenExists() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        eventService.deleteEvent(1L);

        verify(eventRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeleteNotFound() {
        when(eventRepository.existsById(3L)).thenReturn(false);

        assertThatThrownBy(() -> eventService.deleteEvent(3L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event not found with ID: 3");
    }
    
    @Test
    void shouldReturnUpcomingEvents() {
        List<Event> upcoming = Arrays.asList(sampleEvent);
        when(eventRepository.findByEventDateAfter(any(LocalDateTime.class))).thenReturn(upcoming);

        List<Event> result = eventService.getUpcomingEvents();

        assertThat(result).isEqualTo(upcoming);
        verify(eventRepository).findByEventDateAfter(any(LocalDateTime.class));
    }

    @Test
    void shouldReturnPastEvents() {
        List<Event> past = Arrays.asList(sampleEvent);
        when(eventRepository.findByEventDateBefore(any(LocalDateTime.class))).thenReturn(past);

        List<Event> result = eventService.getPastEvents();

        assertThat(result).isEqualTo(past);
        verify(eventRepository).findByEventDateBefore(any(LocalDateTime.class));
    }

    @Test
    void shouldReturnActiveEvents() {
        List<Event> active = Arrays.asList(sampleEvent);
        when(eventRepository.findByActiveTrue()).thenReturn(active);

        List<Event> result = eventService.getActiveEvents();

        assertThat(result).isEqualTo(active);
        verify(eventRepository).findByActiveTrue();
    }

    @Test
    void shouldArchiveEventsOlderThan30Days() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Event oldEvent = new Event("Old Event", "Old event", now.minusDays(40));
        oldEvent.setActive(true);

        Event recentEvent = new Event("Recent Event", "Still valid", now.minusDays(10));
        recentEvent.setActive(true);

        List<Event> allEvents = List.of(oldEvent, recentEvent);

        when(eventRepository.findAll()).thenReturn(allEvents);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        eventService.archiveOldEvents();

        // Then
        assertFalse(oldEvent.getActive(), "Old event should be archived (active = false)");
        assertTrue(recentEvent.getActive(), "Recent event should remain active");

        verify(eventRepository, times(1)).save(oldEvent);
        verify(eventRepository, never()).save(recentEvent);
    }
}
