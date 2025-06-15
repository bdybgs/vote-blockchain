// src/main/java/org/example/entity/Option.java
package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "option")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Каждый вариант (Option) ссылается на одно событие (Event).
     * При сериализации JSON игнорируем поле "options" внутри Event,
     * чтобы не допустить цикла: Option → Event → Option → Event → …
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @JsonIgnoreProperties("options")
    private Event event;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private long votes;
}
