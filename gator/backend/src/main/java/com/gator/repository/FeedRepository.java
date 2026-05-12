package com.gator.repository;

import com.gator.model.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface FeedRepository extends JpaRepository<Feed, UUID> {
    List<Feed> findAllByOrderByCreatedAtDesc();

    /** Return the N oldest-fetched feeds (null lastFetchedAt first). */
    @Query("SELECT f FROM Feed f ORDER BY f.lastFetchedAt ASC NULLS FIRST")
    List<Feed> findNextFeedsToFetch(org.springframework.data.domain.Pageable pageable);
}
