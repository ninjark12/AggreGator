package com.gator.controller;

import com.gator.dto.PostResponse;
import com.gator.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;

    /**
     * Get a paginated feed of posts from feeds the current user follows.
     * ?page=0&size=20
     */
    @GetMapping
    public Page<PostResponse> getMyFeed(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return postRepository.findPostsForUser(userId,
                PageRequest.of(page, Math.min(size, 100)))
                .map(PostResponse::from);
    }

    /** Get posts for a specific feed. */
    @GetMapping("/feed/{feedId}")
    public Page<PostResponse> getByFeed(
            @PathVariable UUID feedId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return postRepository.findByFeedIdOrderByPublishedAtDesc(feedId,
                PageRequest.of(page, Math.min(size, 100)))
                .map(PostResponse::from);
    }
}
