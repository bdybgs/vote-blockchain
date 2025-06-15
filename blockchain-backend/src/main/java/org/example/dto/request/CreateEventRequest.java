// src/main/java/org/example/dto/request/CreateEventRequest.java
package org.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record CreateEventRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull  LocalDateTime startDate,
        @NotNull  LocalDateTime endDate,
        @NotEmpty List<String> options   // тексты вариантов
) {}
