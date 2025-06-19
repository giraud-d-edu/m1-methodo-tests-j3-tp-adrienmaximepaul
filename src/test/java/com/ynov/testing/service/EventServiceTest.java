package com.ynov.testing.service;

import com.ynov.testing.model.Event;
import com.ynov.testing.model.Player;
import com.ynov.testing.model.Team;
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
        Team teamAlpha = new Team();
        teamAlpha.setName("Team Alpha");

        Player alpha1 = new Player("Alice", "Anderson", "alice@example.com", 25, "Attacker");
        Player alpha2 = new Player("Bob", "Brown", "bob@example.com", 28, "Defender");


        Team teamBeta = new Team();
        teamBeta.setName("Team Beta");

        Player beta1 = new Player("Charlie", "Clark", "charlie@example.com", 27, "Midfielder");
        Player beta2 = new Player("Dave", "Dixon", "dave@example.com", 30, "Goalkeeper");

        teamAlpha.setPlayers(List.of(alpha1, alpha2));
        teamBeta.setPlayers(List.of(beta1, beta2));
        sampleEvent.setTeamA(teamAlpha);
        sampleEvent.setTeamB(teamBeta);
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
        assertThat(result.getTeamA().getName()).isEqualTo("Team Alpha");
        assertThat(result.getTeamB().getName()).isEqualTo("Team Beta");
        assertThat(result.getTeamA().getPlayers().stream()
                .map(Player::getFirstName)
                .toList())
                .containsExactly("Alice", "Bob");

        assertThat(result.getTeamB().getPlayers().stream()
                .map(Player::getFirstName)
                .toList())
                .containsExactly("Charlie", "Dave");

        assertThat(result.getCity()).isEqualTo("Paris");

        verify(eventRepository).save(sampleEvent);
    }

    @Test
    void shouldCreateEventWhenTeamsAreProvided() {
        Event withTeams = new Event();
        withTeams.setName("Demi-finale");
        withTeams.setDescription("Match serré");
        withTeams.setEventDate(LocalDateTime.now().plusDays(5));
        Team dragons = new Team();
        dragons.setName("Dragons");

        Team phoenix = new Team();
        phoenix.setName("Phœnix");

        withTeams.setTeamA(dragons);
        withTeams.setTeamB(phoenix);

        when(eventRepository.existsByName("Demi-finale")).thenReturn(false);
        when(eventRepository.save(withTeams)).thenReturn(withTeams);

        Event result = eventService.createEvent(withTeams);

        assertThat(result.getTeamA()).isEqualTo(dragons);
        assertThat(result.getTeamB()).isEqualTo(phoenix);

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
        Team teamA = new Team();
        teamA.setName("Team A");
        teamA.setRegion("EU");
        teamA.setFoundedDate(LocalDateTime.now());

        Team teamB = new Team();
        teamB.setName("Team B");
        teamB.setRegion("EU");
        teamB.setFoundedDate(LocalDateTime.now());
        e1.setTeamA(teamA);
        e1.setTeamB(teamB);
        assertThatThrownBy(() -> eventService.createEvent(e1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event name is required");

        Event e2 = new Event();
        e2.setName("Name");
        e2.setEventDate(LocalDateTime.now());
        Team teamA2 = new Team();
        teamA2.setName("Team A");
        teamA2.setRegion("EU");
        teamA2.setFoundedDate(LocalDateTime.now());

        Team teamB2 = new Team();
        teamB2.setName("Team B");
        teamB2.setRegion("EU");
        teamB2.setFoundedDate(LocalDateTime.now());
        e2.setTeamA(teamA2);
        e2.setTeamB(teamB2);
        assertThatThrownBy(() -> eventService.createEvent(e2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event description is required");

        Event e2b = new Event();
        e2b.setName("Name");
        e2b.setDescription("   ");
        e2b.setEventDate(LocalDateTime.now());
        Team teamA2b = new Team();
        teamA2b.setName("Team A");
        teamA2b.setRegion("EU");
        teamA2b.setFoundedDate(LocalDateTime.now());

        Team teamB2b = new Team();
        teamB2b.setName("Team B");
        teamB2b.setRegion("EU");
        teamB2b.setFoundedDate(LocalDateTime.now());
        e2b.setTeamA(teamA2b);
        e2b.setTeamB(teamB2b);
        assertThatThrownBy(() -> eventService.createEvent(e2b))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event description is required");

        Event e3 = new Event();
        e3.setName("Name");
        e3.setDescription("Desc");
        Team teamA3 = new Team();
        teamA3.setName("Team A");
        teamA3.setRegion("EU");
        teamA3.setFoundedDate(LocalDateTime.now());

        Team teamB3 = new Team();
        teamB3.setName("Team B");
        teamB3.setRegion("EU");
        teamB3.setFoundedDate(LocalDateTime.now());
        e3.setTeamA(teamA3);
        e3.setTeamB(teamB3);
        assertThatThrownBy(() -> eventService.createEvent(e3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event date is required");

        Event e4 = new Event();
        e4.setName("Name");
        e4.setDescription("Desc");
        e4.setEventDate(LocalDateTime.now());
        Team teamB4 = new Team();
        teamB4.setName("Team B");
        teamB4.setRegion("EU");
        teamB4.setFoundedDate(LocalDateTime.now());
        e4.setTeamB(teamB4);
        assertThatThrownBy(() -> eventService.createEvent(e4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both teamA and teamB are required");

        Event e5 = new Event();
        e5.setName("Name");
        e5.setDescription("Desc");
        e5.setEventDate(LocalDateTime.now());
        Team teamA5 = new Team();
        teamA5.setName("Team A");
        teamA5.setRegion("EU");
        teamA5.setFoundedDate(LocalDateTime.now());

        e5.setTeamA(teamA5);
        assertThatThrownBy(() -> eventService.createEvent(e5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both teamA and teamB are required");
    }


    @Test
    void shouldUpdateEventWhenExistsAndValid() {
        Event updated = new Event();
        updated.setName("Updated");
        updated.setDescription("New Desc");
        updated.setEventDate(LocalDateTime.of(2025, 2, 2, 14, 0));
        updated.setActive(false);
        Team teamA = new Team();
        teamA.setName("New Team A");
        teamA.setRegion("EU");
        teamA.setFoundedDate(LocalDateTime.now());

        Team teamB = new Team();
        teamB.setName("New Team B");
        teamB.setRegion("EU");
        teamB.setFoundedDate(LocalDateTime.now());
        updated.setTeamA(teamA);
        updated.setTeamB(teamB);
        Player newA1 = new Player("NewA1", "LastA1", "a1@example.com", 25, "Forward");
        Player newA2 = new Player("NewA2", "LastA2", "a2@example.com", 26, "Midfielder");

        Player newB1 = new Player("NewB1", "LastB1", "b1@example.com", 24, "Defender");
        Player newB2 = new Player("NewB2", "LastB2", "b2@example.com", 27, "Goalkeeper");

        updated.getTeamA().setPlayers(Arrays.asList(newA1, newA2));
        updated.getTeamB().setPlayers(Arrays.asList(newB1, newB2));
        updated.setCity("Lyon");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        Event result = eventService.updateEvent(1L, updated);

        assertThat(result.getName()).isEqualTo("Updated");
        assertThat(result.getDescription()).isEqualTo("New Desc");
        assertThat(result.getEventDate()).isEqualTo(LocalDateTime.of(2025, 2, 2, 14, 0));
        assertThat(result.getActive()).isFalse();
        assertThat(result.getTeamA()).isEqualTo(teamA);
        assertThat(result.getTeamB()).isEqualTo(teamB);
        assertThat(result.getTeamA().getPlayers().stream()
                .map(Player::getFirstName)
                .toList())
                .containsExactly("NewA1", "NewA2");

        assertThat(result.getTeamB().getPlayers().stream()
                .map(Player::getFirstName)
                .toList())
                .containsExactly("NewB1", "NewB2");
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
        Team teamA = new Team();
        teamA.setName("New Team A");
        teamA.setRegion("EU");
        teamA.setFoundedDate(LocalDateTime.now());

        Team teamB = new Team();
        teamB.setName("New Team B");
        teamB.setRegion("EU");
        teamB.setFoundedDate(LocalDateTime.now());
        invalid.setTeamA(teamA);
        invalid.setTeamB(teamB);
        assertThatThrownBy(() -> eventService.updateEvent(1L, invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event name is required");

        Event invalid2 = new Event();
        invalid2.setName("Valid");
        invalid2.setDescription("  ");
        invalid2.setEventDate(LocalDateTime.now());
        Team teamA2 = new Team();
        teamA2.setName("New Team A");
        teamA2.setRegion("EU");
        teamA2.setFoundedDate(LocalDateTime.now());

        Team teamB2 = new Team();
        teamB2.setName("New Team B");
        teamB2.setRegion("EU");
        teamB2.setFoundedDate(LocalDateTime.now());
        invalid2.setTeamA(teamA2);
        invalid2.setTeamB(teamB2);
        assertThatThrownBy(() -> eventService.updateEvent(1L, invalid2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event description is required");

        Event invalid3 = new Event();
        invalid3.setName("Valid");
        invalid3.setDescription("D");
        Team teamA3 = new Team();
        teamA3.setName("New Team A");
        teamA3.setRegion("EU");
        teamA3.setFoundedDate(LocalDateTime.now());

        Team teamB3 = new Team();
        teamB3.setName("New Team B");
        teamB3.setRegion("EU");
        teamB3.setFoundedDate(LocalDateTime.now());
        invalid3.setTeamA(teamA3);
        invalid3.setTeamB(teamB3);
        assertThatThrownBy(() -> eventService.updateEvent(1L, invalid3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event date is required");

        Event invalid4 = new Event();
        invalid4.setName("Valid");
        invalid4.setDescription("D");
        invalid4.setEventDate(LocalDateTime.now());
        Team teamB4 = new Team();
        teamB4.setName("New Team B");
        teamB4.setRegion("EU");
        teamB4.setFoundedDate(LocalDateTime.now());
        invalid4.setTeamB(teamB4);
        assertThatThrownBy(() -> eventService.updateEvent(1L, invalid4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both teamA and teamB are required");

        Event invalid5 = new Event();
        invalid5.setName("Valid");
        invalid5.setDescription("D");
        invalid5.setEventDate(LocalDateTime.now());
        Team teamA5 = new Team();
        teamA5.setName("New Team A");
        teamA5.setRegion("EU");
        teamA5.setFoundedDate(LocalDateTime.now());
        invalid5.setTeamA(teamA5);
        assertThatThrownBy(() -> eventService.updateEvent(1L, invalid5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Both teamA and teamB are required");

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
        Team teamA = new Team();
        teamA.setName("Dragons");
        teamA.setRegion("EU");
        teamA.setFoundedDate(LocalDateTime.now());

        Team teamB = new Team();
        teamB.setName("Phœnix");
        teamB.setRegion("EU");
        teamB.setFoundedDate(LocalDateTime.now());
        e.setTeamA(teamA);
        e.setTeamB(teamB);
        Player alpha1 = new Player("Alice", "Anderson", "alice@example.com", 25, "Attacker");
        Player alpha2 = new Player("Bob", "Brown", "bob@example.com", 28, "Defender");

        Player beta1 = new Player("Charlie", "Clark", "charlie@example.com", 27, "Midfielder");
        Player beta2 = new Player("Dave", "Dixon", "dave@example.com", 30, "Goalkeeper");

        e.getTeamA().setPlayers(List.of(alpha1, alpha2));
        e.getTeamB().setPlayers(List.of(beta1, beta2));
        e.setEventDate(LocalDateTime.of(2025, 7, 1, 20, 0));
        e.setCity("Paris");

        String teaser = eventService.generateTeaser(e);

        String expected = "Dragons vs Phœnix – 2025-07-01T20:00 at Paris. Players: Alice Anderson, Bob Brown vs Charlie Clark, Dave Dixon";
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

    @Test
    void shouldGetTodaysEvents() {
        try(MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class)){
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedNow);

            Event todayEvent = new Event("Today event", "Event today", fixedNow);

            Event tomorrowEvent = new Event("Tomorrow event", "Event tomorrow", fixedNow.plusDays(1));

            LocalDateTime startOfDay = fixedNow.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = fixedNow.toLocalDate().atTime(23, 59, 59);

            when(eventRepository.findByEventDateAfterAndDateBefore(startOfDay, endOfDay))
                .thenReturn(List.of(todayEvent));

            List<Event> todayEvents = eventService.getTodaysEvents();

            assertEquals(1, todayEvents.size());
            
            verify(eventRepository).findByEventDateAfterAndDateBefore(startOfDay, endOfDay);
            
        }
    }
}
