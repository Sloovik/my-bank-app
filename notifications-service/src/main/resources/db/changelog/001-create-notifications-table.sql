-- liquibase formatted sql

-- changeset dev:001-create-notifications-table
CREATE SCHEMA IF NOT EXISTS notifications;

CREATE TABLE IF NOT EXISTS notifications.notifications
(
    id         BIGSERIAL PRIMARY KEY,
    login      VARCHAR(255) NOT NULL,
    message    TEXT         NOT NULL,
    created_at TIMESTAMP    NOT NULL
);
