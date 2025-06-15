// src/main/java/org/example/entity/Vote.java
package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Сущность Vote. Содержит ссылки на Event и User, но игнорирует циклы.
 */
@Data
@Entity
@Table(name = "votes")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Многие голоса принадлежат одному событию.
     * При сериализации JSON игнорируем поле "votes" и "options" внутри Event,
     * чтобы не уйти в бесконечный цикл: Vote → Event → Option → Event → …
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @JsonIgnoreProperties({ "votes", "options" })
    private Event event;

    /**
     * Многие голоса принадлежат одному пользователю.
     * При сериализации JSON игнорируем поле "votes" внутри User,
     * чтобы не уйти в бесконечный цикл: Vote → User → Vote → …
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("votes")
    private User user;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
