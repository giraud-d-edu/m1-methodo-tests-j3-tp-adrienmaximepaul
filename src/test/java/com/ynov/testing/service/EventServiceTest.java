package com.ynov.testing.service;

import com.ynov.testing.model.Event;
import com.ynov.testing.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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

    final LocalDateTime fixedNow = LocalDateTime.of(2025, 6, 19, 10, 0);

    @BeforeEach
    void setUp() {
        // On étend EventService pour contrôler la date
        eventService = new EventService(eventRepository) {
            @Override
            protected LocalDateTime now() {
                return fixedNow;
            }
        };
    }

    @Test
    void shouldArchiveEventsOlderThan30Days() {
        // Given
        Event oldEvent = new Event("Old Event", "Old event", fixedNow.minusDays(40));
        oldEvent.setActive(true);

        Event recentEvent = new Event("Recent Event", "Still valid", fixedNow.minusDays(10));
        recentEvent.setActive(true);

        when(eventRepository.findByEventDateBeforeAndActiveTrue(fixedNow.minusDays(30))).thenReturn(List.of(oldEvent));
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
