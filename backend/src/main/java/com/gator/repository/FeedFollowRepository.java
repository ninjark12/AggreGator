package com.gator.repository;

import com.gator.model.FeedFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedFollowRepository extends JpaRepository<FeedFollow, UUID> {
    List<FeedFollow> findByUserId(UUID userId);
    Optional<FeedFollow> findByUserIdAndFeedId(UUID userId, UUID feedId);
    boolean existsByUserIdAndFeedId(UUID userId, UUID feedId);
}
