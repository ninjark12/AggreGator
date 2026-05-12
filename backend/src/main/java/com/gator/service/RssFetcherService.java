package com.gator.service;

import com.gator.model.Feed;
import com.gator.model.Post;
import com.gator.repository.PostRepository;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RssFetcherService {

    private final PostRepository postRepository;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    /**
     * Fetch all entries from a feed URL and persist new posts.
     * Returns the number of new posts saved.
     */
    public int fetchAndStore(Feed feed) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(feed.getUrl()))
                    .timeout(Duration.ofSeconds(15))
                    .header("User-Agent", "Gator RSS Aggregator/1.0")
                    .GET().build();

            HttpResponse<java.io.InputStream> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed syndFeed = input.build(new XmlReader(response.body()));

            List<Post> newPosts = syndFeed.getEntries().stream()
                    .filter(e -> e.getLink() != null && !postRepository.existsByUrl(e.getLink()))
                    .map(e -> toPost(e, feed))
                    .toList();

            postRepository.saveAll(newPosts);
            log.info("Feed '{}': {} new posts saved", feed.getName(), newPosts.size());
            return newPosts.size();

        } catch (Exception ex) {
            log.warn("Failed to fetch feed '{}' ({}): {}", feed.getName(), feed.getUrl(), ex.getMessage());
            return 0;
        }
    }

    private Post toPost(SyndEntry entry, Feed feed) {
        OffsetDateTime published = entry.getPublishedDate() != null
                ? entry.getPublishedDate().toInstant().atOffset(ZoneOffset.UTC)
                : entry.getUpdatedDate() != null
                    ? entry.getUpdatedDate().toInstant().atOffset(ZoneOffset.UTC)
                    : null;

        String description = null;
        if (entry.getDescription() != null) {
            // Strip basic HTML tags for plain text preview
            description = entry.getDescription().getValue()
                    .replaceAll("<[^>]*>", "")
                    .strip();
            if (description.length() > 1000) {
                description = description.substring(0, 997) + "…";
            }
        }

        return Post.builder()
                .feed(feed)
                .title(entry.getTitle() != null ? entry.getTitle() : "(no title)")
                .url(entry.getLink())
                .description(description)
                .publishedAt(published)
                .build();
    }
}
