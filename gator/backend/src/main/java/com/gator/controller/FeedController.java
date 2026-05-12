package com.gator.controller;

import com.gator.dto.*;
import com.gator.service.FeedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    /** Create a feed (and auto-follow it). Requires X-User-Id header. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeedResponse create(
            @Valid @RequestBody CreateFeedRequest req,
            @RequestHeader("X-User-Id") UUID userId) {
        return FeedResponse.from(feedService.create(req, userId), true);
    }

    /** List all feeds, annotated with whether the calling user follows them. */
    @GetMapping
    public List<FeedResponse> findAll(@RequestHeader("X-User-Id") UUID userId) {
        return feedService.findAll(userId);
    }

    /** Follow an existing feed. */
    @PostMapping("/{feedId}/follow")
    @ResponseStatus(HttpStatus.CREATED)
    public FeedFollowResponse follow(
            @PathVariable UUID feedId,
            @RequestHeader("X-User-Id") UUID userId) {
        return FeedFollowResponse.from(feedService.follow(userId, feedId));
    }

    /** Unfollow a feed. */
    @DeleteMapping("/{feedId}/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollow(
            @PathVariable UUID feedId,
            @RequestHeader("X-User-Id") UUID userId) {
        feedService.unfollow(userId, feedId);
    }

    /** List all feeds the user follows. */
    @GetMapping("/following")
    public List<FeedFollowResponse> getFollowing(@RequestHeader("X-User-Id") UUID userId) {
        return feedService.getFollows(userId).stream().map(FeedFollowResponse::from).toList();
    }
}
