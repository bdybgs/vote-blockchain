// src/main/java/org/example/controller/EventController.java
package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.response.EventDto;
import org.example.entity.Event;
import org.example.repository.EventRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class EventController {

    private final EventRepository repo;

    /**
     * Возвращает список активных событий (EventDto).
     * Репозиторий возвращает только те Event, у которых status = "active".
     */
    @GetMapping
    public List<EventDto> list() {
        List<Event> active = repo.findByStatus("active");
        return active.stream()
                .map(EventDto::of)
                .toList();
    }

    /**
     * Возвращает конкретное событие по id, снова через DTO.
     */
    @GetMapping("/{id}")
    public EventDto byId(@PathVariable long id) {
        Event e = repo.findById(id).orElseThrow();
        return EventDto.of(e);
    }
}
