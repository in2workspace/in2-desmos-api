CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS transactions
(
    id uuid PRIMARY KEY UNIQUE DEFAULT uuid_generate_v4(),
    transaction_id varchar(256),
    created_at TIMESTAMP NOT NULL,
    entity_id varchar(256),
    datalocation varchar(256),
    entity_type varchar(256),
    entity_hash varchar(256),
    status varchar(256),
    trader varchar(256),
    hash varchar(256)
    );

CREATE TABLE IF NOT EXISTS failed_event_transactions
(
    id uuid PRIMARY KEY UNIQUE DEFAULT uuid_generate_v4(),
    transaction_id varchar(256),
    created_at TIMESTAMP NOT NULL,
    entity_id varchar(256),
    datalocation varchar(256),
    entity_type varchar(256),
    iss varchar(256),
    previous_entity_hash varchar(256),
    priority varchar(256)
    );

CREATE TABLE IF NOT EXISTS failed_entity_transactions
(
    id uuid PRIMARY KEY UNIQUE DEFAULT uuid_generate_v4(),
    transaction_id varchar(256),
    created_at TIMESTAMP NOT NULL,
    notification_id BIGINT,
    entity_id varchar(256),
    datalocation varchar(256),
    entity_type varchar(256),
    previous_entity_hash varchar(256),
    entity TEXT,
    timestamp BIGINT,
    priority varchar(256)
    );
