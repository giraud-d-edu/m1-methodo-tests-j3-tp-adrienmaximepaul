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

    final LocalDateTime fixedNow = LocalDateTime.of(2025, 6, 19, 10, 0);

    @BeforeEach
    void setUp() {
        sampleEvent = new Event();
        sampleEvent.setId(1L);
        sampleEvent.setName("Test Event");
        sampleEvent.setDescription("Description");
        sampleEvent.setEventDate(LocalDateTime.of(2025, 1, 1, 12, 0));
        sampleEvent.setActive(true);
        sampleEvent.setTeamA("Team Alpha");
        sampleEvent.setTeamB("Team Beta");
        sampleEvent.setPlayersTeamA(Arrays.asList("Alice", "Bob"));
        sampleEvent.setPlayersTeamB(Arrays.asList("Charlie", "Dave"));
        sampleEvent.setCity("Paris");
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
        assertThat(result.getTeamA()).isEqualTo("Team Alpha");
        assertThat(result.getTeamB()).isEqualTo("Team Beta");
        assertThat(result.getPlayersTeamA()).containsExactly("Alice", "Bob");
        assertThat(result.getPlayersTeamB()).containsExactly("Charlie", "Dave");
        assertThat(result.getCity()).isEqualTo("Paris");

        verify(eventRepository).save(sampleEvent);
    }

    @Test
    void shouldCreateEventWhenTeamsAreProvided() {
        Event withTeams = new Event();
        withTeams.setName("Demi-finale");
        withTeams.setDescription("Match serré");
        withTeams.setEventDate(LocalDateTime.now().plusDays(5));
        withTeams.setTeamA("Dragons");
        withTeams.setTeamB("Phœnix");

        when(eventRepository.existsByName(anyString())).thenReturn(false);
        when(eventRepository.save(withTeams)).thenReturn(withTeams);

        Event result = eventService.createEvent(withTeams);

        assertThat(result.getTeamA()).isEqualTo("Dragons");
        assertThat(result.getTeamB()).isEqualTo("Phœnix");
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
        e1.setTeamA("A");
        e1.setTeamB("B");
        assertThatThrownBy(() -> eventService.createEvent(e1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event name is required");

        Event e2 = new Event();
        e2.setName("Name");
        e2.setEventDate(LocalDateTime.now());
        e2.setTeamA("A");
        e2.setTeamB("B");
        assertThatThrownBy(() -> eventService.createEvent(e2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event description is required");

        Event e2b = new Event();
        e2b.setName("Name");
        e2b.setDescription("   ");
        e2b.setEventDate(LocalDateTime.now());
        e2b.setTeamA("A");
        e2b.setTeamB("B");
        assertThatThrownBy(() -> eventService.createEvent(e2b))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event description is required");

        Event e3 = new Event();
        e3.setName("Name");
        e3.setDescription("Desc");
        e3.setTeamA("A");
        e3.setTeamB("B");
        assertThatThrownBy(() -> eventService.createEvent(e3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event date is required");

        Event e4 = new Event();
        e4.setName("Name");
        e4.setDescription("Desc");
        e4.setEventDate(LocalDateTime.now());
        e4.setTeamB("B");
        assertThatThrownBy(() -> eventService.createEvent(e4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both teamA and teamB are required");

        Event e5 = new Event();
        e5.setName("Name");
        e5.setDescription("Desc");
        e5.setEventDate(LocalDateTime.now());
        e5.setTeamA("A");
        assertThatThrownBy(() -> eventService.createEvent(e5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both teamA and teamB are required");

        Event input = new Event();
        input.setName("Match");
        input.setDescription("Desc");
        input.setEventDate(LocalDateTime.of(2025, 8, 1, 18, 0));
        input.setTeamA(" ");
        input.setTeamB("B");
        input.setPlayersTeamA(List.of("P1"));
        input.setPlayersTeamB(List.of("P2"));
        input.setCity("Lyon");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(input);
        });

        assertEquals("Both teamA and teamB are required", exception.getMessage());

        Event input2 = new Event();
        input2.setName("Match");
        input2.setDescription("Desc");
        input2.setEventDate(LocalDateTime.of(2025, 8, 1, 18, 0));
        input2.setTeamA("A");
        input2.setTeamB(" ");
        input2.setPlayersTeamA(List.of("P1"));
        input2.setPlayersTeamB(List.of("P2"));
        input2.setCity("Lyon");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(input2);
        });

        assertEquals("Both teamA and teamB are required", ex.getMessage());
    }


    @Test
    void shouldUpdateEventWhenExistsAndValid() {
        Event updated = new Event();
        updated.setName("Updated");
        updated.setDescription("New Desc");
        updated.setEventDate(LocalDateTime.of(2025, 2, 2, 14, 0));
        updated.setActive(false);
        updated.setTeamA("New Team A");
        updated.setTeamB("New Team B");
        updated.setPlayersTeamA(Arrays.asList("NewA1", "NewA2"));
        updated.setPlayersTeamB(Arrays.asList("NewB1", "NewB2"));
        updated.setCity("Lyon");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        Event result = eventService.updateEvent(1L, updated);

        assertThat(result.getName()).isEqualTo("Updated");
        assertThat(result.getDescription()).isEqualTo("New Desc");
        assertThat(result.getEventDate()).isEqualTo(LocalDateTime.of(2025, 2, 2, 14, 0));
        assertThat(result.getActive()).isFalse();
        assertThat(result.getTeamA()).isEqualTo("New Team A");
        assertThat(result.getTeamB()).isEqualTo("New Team B");
        assertThat(result.getPlayersTeamA()).containsExactly("NewA1", "NewA2");
        assertThat(result.getPlayersTeamB()).containsExactly("NewB1", "NewB2");
        assertThat(result.getCity()).isEqualTo("Lyon");
    }


    @Test
    void shouldThrowWhenUpdateEventNotFound() {
        when(eventRepository.findById(2L)).thenReturn(Optional.empty());

        Event data = new Event();
        data.setName("N");
        data.setDescription("D");
        data.setEventDate(LocalDateTime.now());

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
        invalid.setTeamA("A");
        invalid.setTeamB("B");
        assertThatThrownBy(() -> eventService.updateEvent(1L, invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event name is required");

        Event invalid2 = new Event();
        invalid2.setName("Valid");
        invalid2.setDescription("  ");
        invalid2.setEventDate(LocalDateTime.now());
        invalid2.setTeamA("A");
        invalid2.setTeamB("B");
        assertThatThrownBy(() -> eventService.updateEvent(1L, invalid2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event description is required");

        Event invalid3 = new Event();
        invalid3.setName("Valid");
        invalid3.setDescription("D");
        invalid3.setTeamA("A");
        invalid3.setTeamB("B");
        assertThatThrownBy(() -> eventService.updateEvent(1L, invalid3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event date is required");

        Event invalid4 = new Event();
        invalid4.setName("Valid");
        invalid4.setDescription("D");
        invalid4.setEventDate(LocalDateTime.now());
        invalid4.setTeamB("B");
        assertThatThrownBy(() -> eventService.updateEvent(1L, invalid4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both teamA and teamB are required");

        Event invalid5 = new Event();
        invalid5.setName("Valid");
        invalid5.setDescription("D");
        invalid5.setEventDate(LocalDateTime.now());
        invalid5.setTeamA("A");
        assertThatThrownBy(() -> eventService.updateEvent(1L, invalid5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both teamA and teamB are required");

        Event noA = new Event();
        noA.setName("Name"); noA.setDescription("Desc");
        noA.setEventDate(LocalDateTime.now());
        noA.setTeamB("B");
        assertThatThrownBy(() -> eventService.updateEvent(1L, noA))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both teamA and teamB are required");

        Event noB = new Event();
        noB.setName("Name"); noB.setDescription("Desc");
        noB.setEventDate(LocalDateTime.now());
        noB.setTeamA("A");
        assertThatThrownBy(() -> eventService.updateEvent(1L, noB))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both teamA and teamB are required");

        Event input = new Event();
        input.setName("Match");
        input.setDescription("Desc");
        input.setEventDate(LocalDateTime.of(2025, 8, 1, 18, 0));
        input.setTeamA(" ");
        input.setTeamB("B");
        input.setPlayersTeamA(List.of("P1"));
        input.setPlayersTeamB(List.of("P2"));
        input.setCity("Lyon");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(input);
        });

        assertEquals("Both teamA and teamB are required", exception.getMessage());

        Event input2 = new Event();
        input2.setName("Match");
        input2.setDescription("Desc");
        input2.setEventDate(LocalDateTime.of(2025, 8, 1, 18, 0));
        input2.setTeamA("A");
        input2.setTeamB(" ");
        input2.setPlayersTeamA(List.of("P1"));
        input2.setPlayersTeamB(List.of("P2"));
        input2.setCity("Lyon");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(input2);
        });

        assertEquals("Both teamA and teamB are required", ex.getMessage());
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
        try (MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedNow);

            Event oldEvent = new Event("Old Event", "Old event", fixedNow.minusDays(40));
            oldEvent.setActive(true);

            Event recentEvent = new Event("Recent Event", "Still valid", fixedNow.minusDays(10));
            recentEvent.setActive(true);

            when(eventRepository.findByEventDateBeforeAndActiveTrue(fixedNow.minusDays(30))).thenReturn(List.of(oldEvent));
            when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

            eventService.archiveOldEvents();

            assertFalse(oldEvent.getActive(), "Old event should be archived (active = false)");
            assertTrue(recentEvent.getActive(), "Recent event should remain active");

            verify(eventRepository, times(1)).save(oldEvent);
            verify(eventRepository, never()).save(recentEvent);
        }
    }

    @Test
    void shouldGenerateTeaserCorrectly() {
        Event e = new Event();
        e.setName("Grande Finale");
        e.setTeamA("Dragons");
        e.setTeamB("Phœnix");
        e.setPlayersTeamA(List.of("Alice", "Bob"));
        e.setPlayersTeamB(List.of("Xavier", "Yasmine"));
        e.setEventDate(LocalDateTime.of(2025, 7, 1, 20, 0));
        e.setCity("Paris");

        String teaser = eventService.generateTeaser(e);

        String expected = "Dragons vs Phœnix – 2025-07-01T20:00 at Paris. " + "Players: [Alice, Bob] vs [Xavier, Yasmine]";
        assertThat(teaser).isEqualTo(expected);
    }




    @Test
    void shouldCancelEventsIfMoreThan24Hours() {
        try (MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedNow);

            Event event = new Event("New Event", "New event", fixedNow.plusDays(2)); // event dans 2 jours
            event.setCanceled(false);
            event.setId(1L);

            when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

            eventService.cancelEvent(event.getId());

            assertTrue(event.isCanceled(), "Event should be canceled");
            verify(eventRepository).save(event);
        }
    }


    @Test
    void shouldThrowWhenCancelEventLessThan24Hours() {
        try (MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedNow);

            Event event = new Event("New Event", "New event", fixedNow.plusHours(23)); // moins de 24h
            event.setCanceled(false);
            event.setId(1L);

            when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

            assertThatThrownBy(() -> eventService.cancelEvent(event.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("You can't cancel an event less than 24 hours before it starts.");

            verify(eventRepository, never()).save(any());
        }
    }


    @Test
    void shouldThrowWhenCancelEventWithNullOrNonPositiveOrEventNotFound() {
        when(eventRepository.findById(999999999999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.cancelEvent(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event ID must be positive");

        assertThatThrownBy(() -> eventService.cancelEvent(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event ID must be positive");

        assertThatThrownBy(() -> eventService.cancelEvent(999999999999999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event not found with ID: 999999999999999");
    }
}
