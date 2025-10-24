package com.umamusume.backend.dto;

import com.umamusume.backend.entity.User;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponseDto(
    UUID id,
    String username,
    OffsetDateTime createdAt
) {
    public static UserResponseDto fromEntity(User user) {
        return new UserResponseDto(
            user.getId(),
            user.getUsername(),
            user.getCreatedAt()
        );
    }
}
