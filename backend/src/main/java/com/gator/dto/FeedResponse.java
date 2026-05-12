package com.gator.dto;
import com.gator.model.Feed;
import java.time.OffsetDateTime;
import java.util.UUID;

public record FeedResponse(UUID id, String name, String url, UUID userId,
                           String userName, OffsetDateTime lastFetchedAt,
                           OffsetDateTime createdAt, boolean following) {
    public static FeedResponse from(Feed f, boolean following) {
        return new FeedResponse(f.getId(), f.getName(), f.getUrl(),
            f.getUser().getId(), f.getUser().getName(),
            f.getLastFetchedAt(), f.getCreatedAt(), following);
    }
}
