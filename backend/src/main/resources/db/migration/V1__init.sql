-- ── Users ─────────────────────────────────────────────────────────────────────
CREATE TABLE users (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ── Feeds ─────────────────────────────────────────────────────────────────────
CREATE TABLE feeds (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(255) NOT NULL,
    url             TEXT         NOT NULL UNIQUE,
    user_id         UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    last_fetched_at TIMESTAMPTZ,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_feeds_user_id ON feeds(user_id);
CREATE INDEX idx_feeds_last_fetched_at ON feeds(last_fetched_at NULLS FIRST);

-- ── Feed Follows ──────────────────────────────────────────────────────────────
CREATE TABLE feed_follows (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    feed_id    UUID        NOT NULL REFERENCES feeds(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, feed_id)
);

CREATE INDEX idx_feed_follows_user_id ON feed_follows(user_id);

-- ── Posts ─────────────────────────────────────────────────────────────────────
CREATE TABLE posts (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    feed_id      UUID         NOT NULL REFERENCES feeds(id) ON DELETE CASCADE,
    title        TEXT         NOT NULL,
    url          TEXT         NOT NULL UNIQUE,
    description  TEXT,
    published_at TIMESTAMPTZ,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_posts_feed_id ON posts(feed_id);
CREATE INDEX idx_posts_published_at ON posts(published_at DESC);
