package com.sdatfinals.backend.user;

import java.time.Instant;

public record UserResponse(
        Long id,
        String name,
        String email,
        String role,
        Instant createdAt,
        Instant lastLoginAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
    }
}
