CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS audit_records
(
    id              uuid PRIMARY KEY UNIQUE DEFAULT uuid_generate_v4(),
    process_id      varchar(256),
    created_at      TIMESTAMP NOT NULL,
    entity_id       varchar(256),
    entity_type     varchar(256),
    entity_hash     varchar(256),
    entity_hashlink varchar(256),
    data_location   varchar(256),
    status          varchar(256),
    trader          varchar(256),
    hash            varchar(256),
    hashlink        varchar(256)
);

CREATE TABLE IF NOT EXISTS tx_payload_recover
(
    id              uuid PRIMARY KEY UNIQUE DEFAULT uuid_generate_v4(),
    process_id         varchar(256),
    eventType          varchar(256),
    iss                varchar(256),
    entityId           varchar(256),
    previousEntityHash varchar(256),
    dataLocation       varchar(256),
    relevantMetadata   TEXT,
    eventQueuePriority varchar(256)
);

CREATE TABLE IF NOT EXISTS blockchain_notification_recover
(
    id              uuid PRIMARY KEY UNIQUE DEFAULT uuid_generate_v4(),
    process_id         varchar(256),
    notification_id          varchar(256),
    publisherAddress                varchar(256),
    eventType           varchar(256),
    timestamp BIGINT,
    dataLocation       varchar(256),
    relevantMetadata   TEXT,
    entityIdHash varchar(256),
    previousEntityHash varchar(256),
    eventQueuePriority varchar(256)

    );

