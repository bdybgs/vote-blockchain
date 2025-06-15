// src/main/java/org/example/entity/User.java
package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сущность User. Скрываем поле password и не сериализуем votes,
 * чтобы GET /api/users/profile сразу возвращал только id, username, email, role, joinDate.
 */
@Data
@Entity
@Table(name = "users")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    /** Не отдаем пароль в JSON */
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(name = "join_date", nullable = false)
    private LocalDateTime joinDate;

    /**
     * Один пользователь может иметь несколько Vote.
     * Но для вывода профиля (GET /api/users/profile) мы не хотим отдавать
     * всю историю голосов, чтобы не углубляться в циклы.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Vote> votes;
}
