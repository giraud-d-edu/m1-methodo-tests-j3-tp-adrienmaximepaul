package com.ynov.testing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event Entity - Simple entity for testing purposes
 *
 * This entity represents a basic event with minimal fields
 * to focus on testing fundamentals and date mocking.
 *
 * @author Testing Methodology Course
 * @version 1.0.0
 */
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Event name is required")
    @Size(min = 2, max = 100, message = "Event name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Event date is required")
    private LocalDateTime eventDate;

    @Column(name = "is_active")
    private Boolean active = true;

    private String teamA;
    private String teamB;
    private List<String> playersTeamA;
    private List<String> playersTeamB;
    private String city;
    private String teaser;

    // Constructeurs
    public Event() {}

    public Event(String name, String description, LocalDateTime eventDate) {
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getTeamA() { return teamA; }
    public void setTeamA(String teamA) { this.teamA = teamA; }
    public String getTeamB() { return teamB; }
    public void setTeamB(String teamB) { this.teamB = teamB; }
    public List<String> getPlayersTeamA() { return playersTeamA; }
    public void setPlayersTeamA(List<String> playersTeamA) { this.playersTeamA = playersTeamA; }
    public List<String> getPlayersTeamB() { return playersTeamB; }
    public void setPlayersTeamB(List<String> playersTeamB) { this.playersTeamB = playersTeamB; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getTeaser() { return teaser; }
    public void setTeaser(String teaser) { this.teaser = teaser; }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", eventDate=" + eventDate +
                ", active=" + active +
                '}';
    }

}
