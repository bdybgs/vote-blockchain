// src/main/java/org/example/dto/response/EventDto.java
package org.example.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.entity.Event;
import org.example.entity.Option;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Голосование (event)")
public record EventDto(
        Long              id,
        String            title,
        String            description,
        LocalDateTime     endDate,
        long              totalVotes,
        String            status,        // "active" | "completed"
        List<OptionDto>   options
) {
    public static EventDto of(Event e) {

        long sum = e.getOptions().stream()
                .mapToLong(Option::getVotes)
                .sum();

        String status = e.getEndDate().isBefore(LocalDateTime.now())
                ? "completed"
                : "active";

        return new EventDto(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getEndDate(),
                sum,
                status,
                e.getOptions().stream().map(OptionDto::of).toList()
        );
    }

    public record OptionDto(Long id, String text, long votes) {
        public static OptionDto of(Option o) {
            return new OptionDto(o.getId(), o.getText(), o.getVotes());
        }
    }
}
