package com.gator.repository;

import com.gator.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    boolean existsByUrl(String url);

    /** Posts from feeds the user follows, newest first. */
    @Query("""
        SELECT p FROM Post p
        JOIN FeedFollow ff ON ff.feed.id = p.feed.id
        WHERE ff.user.id = :userId
        ORDER BY p.publishedAt DESC NULLS LAST
    """)
    Page<Post> findPostsForUser(@Param("userId") UUID userId, Pageable pageable);

    /** All posts for a specific feed, newest first. */
    Page<Post> findByFeedIdOrderByPublishedAtDesc(UUID feedId, Pageable pageable);
}
