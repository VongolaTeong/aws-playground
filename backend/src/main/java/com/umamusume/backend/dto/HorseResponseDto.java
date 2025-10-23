package com.umamusume.backend.dto;

import com.umamusume.backend.entity.Horse;
import com.umamusume.backend.entity.User;

import java.time.OffsetDateTime;
import java.util.UUID;

public record HorseResponseDto(
    UUID id,
    String name,
    UserResponseDto owner,
    OffsetDateTime createdAt
) {
    public static HorseResponseDto fromEntity(Horse horse) {
        return new HorseResponseDto(
            horse.getId(),
            horse.getName(),
            UserResponseDto.fromEntity(horse.getOwner()),
            horse.getCreatedAt()
        );
    }
}
