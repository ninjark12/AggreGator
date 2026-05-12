package com.gator.dto;
import com.gator.model.FeedFollow;
import java.time.OffsetDateTime;
import java.util.UUID;

public record FeedFollowResponse(UUID id, UUID userId, UUID feedId, String feedName,
                                  String feedUrl, OffsetDateTime createdAt) {
    public static FeedFollowResponse from(FeedFollow ff) {
        return new FeedFollowResponse(ff.getId(), ff.getUser().getId(),
            ff.getFeed().getId(), ff.getFeed().getName(), ff.getFeed().getUrl(),
            ff.getCreatedAt());
    }
}
