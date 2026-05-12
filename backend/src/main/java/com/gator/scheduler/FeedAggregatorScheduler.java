package com.gator.scheduler;

import com.gator.model.Feed;
import com.gator.repository.FeedRepository;
import com.gator.service.RssFetcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FeedAggregatorScheduler {

    private final FeedRepository feedRepository;
    private final RssFetcherService rssFetcherService;

    // Fetch up to 10 feeds per tick to avoid hammering the network
    private static final int BATCH_SIZE = 10;

    @Scheduled(fixedDelayString = "${gator.fetch.interval-ms:300000}")
    @Transactional
    public void aggregateFeeds() {
        List<Feed> feeds = feedRepository.findNextFeedsToFetch(PageRequest.of(0, BATCH_SIZE));
        if (feeds.isEmpty()) {
            log.debug("No feeds to aggregate");
            return;
        }
        log.info("Aggregating {} feeds...", feeds.size());
        int totalNew = 0;
        for (Feed feed : feeds) {
            int newPosts = rssFetcherService.fetchAndStore(feed);
            feed.setLastFetchedAt(OffsetDateTime.now());
            feedRepository.save(feed);
            totalNew += newPosts;
        }
        log.info("Aggregation complete. {} new posts saved across {} feeds.", totalNew, feeds.size());
    }
}
