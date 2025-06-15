// src/main/java/org/example/entity/Block.java
package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "blocks")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "block_number", nullable = false)
    private Long number;

    @Column(name = "previous_hash", nullable = false)
    private String previousHash;

    @Column(name = "current_hash", nullable = false)
    private String currentHash;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    /**
     * Один блок относится к одному событию.
     * При сериализации JSON игнорируем поля "options" и "votes" внутри Event
     * (они уже не нужны в Explorer), чтобы не зациклиться: Block → Event → Option/Vote → Event …
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnoreProperties({ "options", "votes" })
    private Event event;

    /**
     * Один блок — это голос, отданный одним пользователем.
     * При сериализации JSON игнорируем поле "votes" внутри User, чтобы не уйти
     * в бесконечный цикл: Block → User → Vote → User …
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", nullable = false)
    @JsonIgnoreProperties("votes")
    private User voter;

    @Column(name = "total_voters", nullable = false)
    private Integer totalVoters = 0;

    @Column(name = "signature", nullable = false)
    private byte[] signature;
}
