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
    ('721421a4-d333-40fe-a192-af4265a3eedb', '0', '2024-05-09 16:51:40.403246', 'urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87', 'ProductOffering', '60ed4dab7d1c30baf865a698443383e9cf96a5496f635a89ca58839e822c2240', 'fa544b34af6221ea9fd2f306c8d90e7a04e5020fd9a137180702e23f694281b8bc4e60ed4dab7d1c30baf865a698443383e9cf96a5496f635a89ca58839e822c2240', 'http://localhost:55555/ngsi-ld/v1/entities/urn:ProductOffering:ed9c56c8-a5ab-42cc-bc62-0fca69a30c87?7b1f2e439e03fdc0d1d8ab65b0408c55c42d5e120c6f1f8f71a06aa287b8f056', 'PUBLISHED', 'CONSUMER', '605285d9d71387727dfcfbc2acf8b02a23f57b9857f33ad61bb96c1cf72c9502', 'b0703853ec9dcc1b7f13f7995ce3091efbf79820f694c3024fb64750aa4c23eb'),
    ('fd1421a4-d333-40fe-a192-af4265a3eedb', '0', '2024-05-09 16:51:40.403241', 'urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5b', 'ProductOfferingPrice', '9a69533ae44995f511c926b6f443e9e9738041421cf5ddf0c8f5b31ffde310cf', 'fa549a69533ae44995f511c926b6f443e9e9738041421cf5ddf0c8f5b31ffde310cf', 'http://localhost:55555/ngsi-ld/v1/entities/ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5b?9a69533ae44995f511c926b6f443e9e9738041421cf5ddf0c8f5b31ffde310cf', 'PUBLISHED', 'CONSUMER', '605285d9d71387727dfcfbc2acf8b02a23f57b9857f33ad61bb96c1cf72c9502', 'b0703853ec9dcc1b7f13f7995ce3091efbf79820f694c3024fb64750aa4c23eb'),
    ('ba319e5d-61d6-4d69-9c3d-f15141959c11', '0', '2024-05-09 16:51:40.43879', 'urn:ProductOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c', 'ProductOffering', 'c91e4fb89b21afca059a879d5d936f61331ecdbb2f61546af1c4abbafe6f27c6', 'fa54c91e4fb89b21afca059a879d5d936f61331ecdbb2f61546af1c4abbafe6f27c6', 'http://localhost:55555/ngsi-ld/v1/entities/urn:ProductOffering:3645a0de-d74f-42c5-86ab-e27ccbdf0a9c?c91e4fb89b21afca059a879d5d936f61331ecdbb2f61546af1c4abbafe6f27c6', 'PUBLISHED', 'CONSUMER', 'd46a4c3187ff33bc0927aef12cc3e4955264ae37b27a7400a8aa43abddf4f36d', 'f76a738130453a2d94a106d58cdfd7a210552db163d4b29f4e5dc4204c5215b5'),
    ('2af8cae8-0297-44f4-a15f-dbb806e10fbd', '0', '2024-05-09 16:51:40.54389', 'urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a', 'ProductOfferingPrice', '7c0cd001f472cd991c12fdf82683efa727016bf49c0eee24feb96cc22a1ab6f8', 'fa547c0cd001f472cd991c12fdf82683efa727016bf49c0eee24feb96cc22a1ab6f8', 'http://localhost:55555/ngsi-ld/v1/entities/urn:ProductOfferingPrice:912efae1-7ff6-4838-89f3-cfedfdfa1c5a?7c0cd001f472cd991c12fdf82683efa727016bf49c0eee24feb96cc22a1ab6f8', 'PUBLISHED', 'CONSUMER', '1', '11'),
    ('2bc632e8-3066-4290-9850-272262ffabdd', '0', '2024-05-09 16:51:40.12345', 'urn:Price:2d5f3c16-4e77-45b3-8915-3da36b714e7b', 'Price', 'f2ca059930791fcddaa387480cd722c64ba31d816a0255c2f89bf4b28def7680', 'fa54f2ca059930791fcddaa387480cd722c64ba31d816a0255c2f89bf4b28def7680', 'http://localhost:55555/ngsi-ld/v1/entities/urn:Price:2d5f3c16-4e77-45b3-8915-3da36b714e7b?f2ca059930791fcddaa387480cd722c64ba31d816a0255c2f89bf4b28def7680', 'PUBLISHED', 'CONSUMER', '1', '11');