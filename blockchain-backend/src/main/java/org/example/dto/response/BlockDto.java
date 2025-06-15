// src/main/java/org/example/dto/response/BlockDto.java
package org.example.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.entity.Block;

import java.time.LocalDateTime;

@Schema(description = "Блок в цепочке")
public record BlockDto(
        Long           number,
        String         hash,
        LocalDateTime  timestamp,
        Integer        totalVoters,
        String         voter,
        String         event
) {
    public static BlockDto of(Block b) {
        return new BlockDto(
                b.getNumber(),
                b.getCurrentHash(),
                b.getTimestamp(),
                b.getTotalVoters(),
                b.getVoter()  != null ? b.getVoter().getUsername() : null,
                b.getEvent()  != null ? b.getEvent().getTitle()    : null
        );
    }
}
