package com.gator.dto;
import com.gator.model.Post;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PostResponse(UUID id, UUID feedId, String feedName, String title,
                           String url, String description, OffsetDateTime publishedAt) {
    public static PostResponse from(Post p) {
        return new PostResponse(p.getId(), p.getFeed().getId(), p.getFeed().getName(),
            p.getTitle(), p.getUrl(), p.getDescription(), p.getPublishedAt());
    }
}
