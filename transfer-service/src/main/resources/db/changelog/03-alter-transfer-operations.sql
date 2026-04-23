-- liquibase formatted sql
-- changeset dev:3

ALTER TABLE transfer.transfer_operations
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    ADD COLUMN IF NOT EXISTS error_message TEXT;
