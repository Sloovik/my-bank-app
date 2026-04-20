-- liquibase formatted sql

-- changeset dev:002-insert-test-data
INSERT INTO accounts.accounts (login, name, birthdate, balance)
VALUES ('user1', 'Иванов Иван', '1990-01-15', 100.00),
       ('user2', 'Петров Петр', '1985-06-20', 100.00)
ON CONFLICT (login) DO NOTHING;
