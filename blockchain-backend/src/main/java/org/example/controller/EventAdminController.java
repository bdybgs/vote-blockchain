// src/main/java/org/example/controller/EventAdminController.java
package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.CreateEventRequest;
import org.example.entity.Event;
import org.example.entity.Option;
import org.example.repository.EventRepository;
import org.example.repository.OptionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@Validated
public class EventAdminController {

    private final EventRepository eventRepo;
    private final OptionRepository optionRepo;

    /**
     * Админ-эндпоинт для создания нового голосования (события)
     * (требует роль ADMIN).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody @Validated CreateEventRequest req) {
        // 1) Сохраняем сам Event
        Event e = new Event();
        e.setTitle(req.title());
        e.setDescription(req.description());
        e.setStartDate(req.startDate());
        e.setEndDate(req.endDate());
        e.setStatus("active");
        e = eventRepo.save(e);

        // 2) Сохраняем варианты (Option) для этого события
        List<Option> opts = new ArrayList<>();
        for (String text : req.options()) {
            Option o = new Option();
            o.setEvent(e);
            o.setText(text);
            o.setVotes(0L);
            opts.add(o);
        }
        optionRepo.saveAll(opts);

        return ResponseEntity.status(201).body(e);
    }
}
