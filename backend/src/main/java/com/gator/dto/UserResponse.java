package com.gator.dto;
import com.gator.model.User;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(UUID id, String name, OffsetDateTime createdAt) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getCreatedAt());
    }
}
