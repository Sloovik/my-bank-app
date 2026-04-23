-- liquibase formatted sql
-- changeset dev:3

CREATE TABLE IF NOT EXISTS accounts.outbox_events (
    id          BIGSERIAL PRIMARY KEY,
    event_type  VARCHAR(100) NOT NULL,
    payload     TEXT NOT NULL,
    processed   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP,
    processed_at TIMESTAMP
    );
