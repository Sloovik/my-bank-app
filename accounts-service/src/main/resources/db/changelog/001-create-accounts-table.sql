-- liquibase formatted sql

-- changeset dev:001-create-accounts-table
CREATE SCHEMA IF NOT EXISTS accounts;

CREATE TABLE IF NOT EXISTS accounts.accounts
(
    id        BIGSERIAL PRIMARY KEY,
    login     VARCHAR(255) NOT NULL UNIQUE,
    name      VARCHAR(255) NOT NULL,
    birthdate DATE         NOT NULL,
    balance   NUMERIC(19, 2) NOT NULL DEFAULT 0
);
