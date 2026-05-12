package com.gator.service;

import com.gator.dto.CreateFeedRequest;
import com.gator.dto.FeedResponse;
import com.gator.model.Feed;
import com.gator.model.FeedFollow;
import com.gator.model.User;
import com.gator.repository.FeedFollowRepository;
import com.gator.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final FeedFollowRepository feedFollowRepository;
    private final UserService userService;

    @Transactional
    public Feed create(CreateFeedRequest req, UUID userId) {
        User user = userService.findById(userId);
        Feed feed = Feed.builder()
                .name(req.name())
                .url(req.url())
                .user(user)
                .build();
        Feed saved = feedRepository.save(feed);
        // auto-follow on creation
        feedFollowRepository.save(FeedFollow.builder().user(user).feed(saved).build());
        return saved;
    }

    public List<FeedResponse> findAll(UUID currentUserId) {
        Set<UUID> followed = feedFollowRepository.findByUserId(currentUserId)
                .stream().map(ff -> ff.getFeed().getId()).collect(Collectors.toSet());
        return feedRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(f -> FeedResponse.from(f, followed.contains(f.getId())))
                .toList();
    }

    public Feed findById(UUID id) {
        return feedRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feed not found"));
    }

    @Transactional
    public FeedFollow follow(UUID userId, UUID feedId) {
        User user = userService.findById(userId);
        Feed feed = findById(feedId);
        if (feedFollowRepository.existsByUserIdAndFeedId(userId, feedId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already following this feed");
        }
        return feedFollowRepository.save(FeedFollow.builder().user(user).feed(feed).build());
    }

    @Transactional
    public void unfollow(UUID userId, UUID feedId) {
        FeedFollow ff = feedFollowRepository.findByUserIdAndFeedId(userId, feedId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Not following this feed"));
        feedFollowRepository.delete(ff);
    }

    public List<FeedFollow> getFollows(UUID userId) {
        return feedFollowRepository.findByUserId(userId);
    }
}
