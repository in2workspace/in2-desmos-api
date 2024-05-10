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

INSERT INTO audit_records (id, process_id, created_at, entity_id, entity_type, entity_hash, entity_hashlink, data_location, status, trader, hash, hashlink)
VALUES
    ('721421a4-d333-40fe-a192-af4265a3eedb', '0', '2024-05-09 16:51:40.403246', 'urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87', 'ProductOffering', 'eb2a3f823a26b77562ed81e9a6d1e8f9cbd5c9dd5f89b39923dfae7fd47ac818', 'fa544b34af6221ea9fd2f306c8d90e7a04e5020fd9a137180702e23f694281b8bc4eeb2a3f823a26b77562ed81e9a6d1e8f9cbd5c9dd5f89b39923dfae7fd47ac818', 'http://localhost:55555/ngsi-ld/v1/entities/urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87?08b236983ba01bbcd268793b104917f89f0bba8160d2f693911087c72b9a8051', 'PUBLISHED', 'CONSUMER', '605285d9d71387727dfcfbc2acf8b02a23f57b9857f33ad61bb96c1cf72c9502', 'b0703853ec9dcc1b7f13f7995ce3091efbf79820f694c3024fb64750aa4c23eb'),
    ('ba319e5d-61d6-4d69-9c3d-f15141959c11', '0', '2024-05-09 16:51:40.43879', 'urn:ProductOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c', 'ProductOffering', 'f7e776655017d297dbf4a845db5d12595ba927460023c14bff1215acef95ec12', 'fa54f7e776655017d297dbf4a845db5d12595ba927460023c14bff1215acef95ec12', 'http://localhost:55555/ngsi-ld/v1/entities/urn:ProductOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c?cbcd525a6f2b5b6cefd8b4d9e83ac1e32946f5388634f012736bd6d543f0d017', 'PUBLISHED', 'CONSUMER', 'd46a4c3187ff33bc0927aef12cc3e4955264ae37b27a7400a8aa43abddf4f36d', 'f76a738130453a2d94a106d58cdfd7a210552db163d4b29f4e5dc4204c5215b5');