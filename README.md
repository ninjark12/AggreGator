# 🐊 Gator — RSS Feed Aggregator

Full-stack RSS aggregator built with **Spring Boot** (backend) and **React** (frontend).

## Architecture

```
gator/
├── backend/          ← Spring Boot 3.2 + Java 21
│   ├── pom.xml
│   └── src/main/java/com/gator/
│       ├── model/         User, Feed, FeedFollow, Post
│       ├── repository/    Spring Data JPA repositories
│       ├── service/       UserService, FeedService, RssFetcherService
│       ├── controller/    REST endpoints
│       ├── scheduler/     FeedAggregatorScheduler (auto-fetches every 5min)
│       └── dto/           Request/Response records
└── frontend/         ← React 18 + Vite
    └── src/
        ├── api/client.js  Typed API wrapper
        └── pages/         LoginPage, PostsPage, FeedsPage
```

## Quick Start

### 1. Prerequisites

- Java 21+
- PostgreSQL 15+
- Node.js 18+

### 2. Database

```bash
createdb gator
# or with psql:
psql -U postgres -c "CREATE DATABASE gator;"
```

Flyway will run `V1__init.sql` automatically on first startup.

### 3. Backend

```bash
cd backend

# Edit connection settings if needed:
# src/main/resources/application.properties

mvn spring-boot:run
# → Starts on http://localhost:8080
```

### 4. Frontend

```bash
cd frontend
npm install
npm run dev
# → Opens http://localhost:5173
```

## REST API Reference

All endpoints under `/api/`. Authenticated endpoints require `X-User-Id: <uuid>` header.

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/api/users` | — | Register a user |
| `GET` | `/api/users` | — | List all users |
| `GET` | `/api/feeds` | ✓ | List all feeds (with follow status) |
| `POST` | `/api/feeds` | ✓ | Add a feed (auto-follows it) |
| `POST` | `/api/feeds/{id}/follow` | ✓ | Follow a feed |
| `DELETE` | `/api/feeds/{id}/follow` | ✓ | Unfollow a feed |
| `GET` | `/api/feeds/following` | ✓ | List your followed feeds |
| `GET` | `/api/posts?page=0&size=20` | ✓ | Paginated posts from followed feeds |
| `GET` | `/api/posts/feed/{feedId}` | ✓ | Posts for a specific feed |

## How Feed Aggregation Works

1. `FeedAggregatorScheduler` runs every 5 minutes (configurable via `gator.fetch.interval-ms`)
2. It picks the 10 feeds with the oldest `last_fetched_at` (null first)
3. `RssFetcherService` fetches each URL using Java's `HttpClient` and parses it with **Rome**
4. New posts are saved; duplicate URLs are skipped
5. `last_fetched_at` is updated so the scheduler cycles through all feeds fairly

## Sample RSS Feeds to Try

```
https://feeds.feedburner.com/TheDailyWtf        The Daily WTF
https://hnrss.org/frontpage                     Hacker News
https://www.reddit.com/r/programming/.rss       Reddit /r/programming
https://css-tricks.com/feed/                    CSS-Tricks
https://blog.golang.org/feed.atom               Go Blog
```

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/gator` | DB URL |
| `spring.datasource.username` | `postgres` | DB username |
| `spring.datasource.password` | `postgres` | DB password |
| `gator.fetch.interval-ms` | `300000` | Feed poll interval (ms) |
| `gator.cors.allowed-origins` | `http://localhost:5173` | CORS origins |
