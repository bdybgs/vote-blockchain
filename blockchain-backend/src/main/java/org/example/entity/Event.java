// src/main/java/org/example/entity/Event.java
package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "events")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private String status = "active"; // "active" или "completed"

    /**
     * Список вариантов (Option).
     * При сериализации JSON игнорируем поле "event" внутри каждого Option,
     * чтобы избежать циклического перехода: Event → Option → Event → Option → …
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("event")
    private List<Option> options;

    /**
     * Список голосов (Vote).
     * При сериализации JSON игнорируем поле "event" внутри каждого Vote,
     * чтобы избежать циклического перехода: Event → Vote → Event → Vote → …
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("event")
    private List<Vote> votes;
}
