package es.in2.desmos.domain.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import es.in2.desmos.ContainerManager;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.AuditRecordTrader;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.TimeZone;
import java.util.UUID;

import static es.in2.desmos.domain.utils.ApplicationUtils.calculateHashLink;
import static es.in2.desmos.domain.utils.ApplicationUtils.calculateSHA256;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuditRecordRepositoryIT {

    @Autowired
    private AuditRecordRepository auditRecordRepository;

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    @Before
    public void cleanUp() {
        auditRecordRepository.deleteAll().block();
    }

    @BeforeEach
    void setUp() {
        objectMapper.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES).build();
    private final AuditRecord auditRecordRoot = AuditRecord.builder()
            .id(UUID.fromString("5d72b588-5257-46a0-8636-cf9226c8ebc6"))
            .processId("f3a387e5-c862-4b93-b5f8-d80f83b0e400")
            .createdAt(new Timestamp(0L))
            .entityId("urn:ngsi-ld:ProductOffering:8574a163-6a3d-4fa6-94cc-17e877ec0230")
            .entityType("ProductOffering")
            .entityHash("f8638b979b2f4f793ddb6dbd197e0ee25a7a6ea32b0ae22f5e3c5d119d839e75") // 5678
            .entityHashLink("cbb5d4ada62f263a6c653fc123e09dccb652d55baa1fec215bf03f81d76b97af") // 1234+5678
            .dataLocation("https://domain.org/ngsi-ld/v1/entities/" +
                    "urn:ngsi-ld:ProductOffering:8574a163-6a3d-4fa6-94cc-17e877ec0230" +
                    "?hl=cbb5d4ada62f263a6c653fc123e09dccb652d55baa1fec215bf03f81d76b97af")
            .status(AuditRecordStatus.CREATED)
            .trader(AuditRecordTrader.PRODUCER)
            .hash("")
            .hashLink("")
            .newTransaction(true)
            .build();
    private final AuditRecord auditRecord = AuditRecord.builder()
            .id(UUID.fromString("ae277aa0-7677-4038-acf6-52a8e70c4d04"))
            .processId("14f121af-d720-4a53-bc08-fc00bdbbbebe")
            .createdAt(new Timestamp(0L))
            .entityId("urn:ngsi-ld:ProductOffering:6e00d349-4c49-4bbe-83a9-65115f144908")
            .entityType("ProductOffering")
            .entityHash("a60394397a82adadb646b4cf20c1caa3a2209cbe68e0a898fc3d6cd2008cb2fa") // 9862
            .entityHashLink("56ba5b3c6f0cb990346dd5bc37f4752229c7e712abc8a0ddd16db5eeba711645") // 5284+9862
            .dataLocation("https://domain.org/ngsi-ld/v1/entities/" +
                    "urn:ngsi-ld:ProductOffering:8574a163-6a3d-4fa6-94cc-17e877ec0230" +
                    "?hl=56ba5b3c6f0cb990346dd5bc37f4752229c7e712abc8a0ddd16db5eeba711645")
            .status(AuditRecordStatus.CREATED)
            .trader(AuditRecordTrader.PRODUCER)
            .hash("")
            .hashLink("")
            .newTransaction(true)
            .build();

    @Order(0)
    @Test
    void shouldSaveAuditRecordRoot() throws JsonProcessingException, NoSuchAlgorithmException {
        // Arrange
        String expectedAuditRecordRootHash = "f291c0096b7c3e10a52db72ada76676ed2a928b7fd9e91ab9f1ccb7614d8bd08";
        // Calculate the hash of the AuditRecordRoot and set them.
        // The hashLink is the hash of the AuditRecordRoot because it is the first record
        String auditRecordRootHash = calculateSHA256(objectMapper.writeValueAsString(auditRecordRoot));
        System.out.println("auditRecordRoot:" + objectMapper.writeValueAsString(auditRecordRoot));
        auditRecordRoot.setHash(auditRecordRootHash);
        auditRecordRoot.setHashLink(auditRecordRootHash);
        // Save AuditRecordRoot
        AuditRecord auditRecordRootMono = auditRecordRepository.save(auditRecordRoot).block();
        // Assertion for AuditRecordRoot
        assert auditRecordRootMono != null;
        // The hash of the AuditRecordRoot is the hash expected and calculated previously
        System.out.println("auditRecordRootHash: " + auditRecordRootHash);
        System.out.println(auditRecordRootMono);
        assertEquals(expectedAuditRecordRootHash, auditRecordRootMono.getHash());
        // The hash of the AuditRecordRoot is the same as the hash returned by the database
        assertEquals(auditRecordRoot.getHash(), auditRecordRootMono.getHash());
        assert auditRecordRoot.getId() != null;
        AuditRecord fetchedAuditRecord = auditRecordRepository.findById(auditRecordRoot.getId()).block();
        assertNotNull(fetchedAuditRecord, "Entity must be saved in the database and not be null.");
        assertEquals(auditRecordRoot.getHash(), fetchedAuditRecord.getHash(), "Hash must be the same as the one saved in the database.");
    }

    @Order(1)
    @Test
    void shouldSaveAuditRecordGuaranteeImmutability() throws JsonProcessingException, NoSuchAlgorithmException {
        /*
         * HashLink set in test 0 = f291c0096b7c3e10a52db72ada76676ed2a928b7fd9e91ab9f1ccb7614d8bd08
         * Hash calculated for new AuditRecord = 8008569e61c6bea20c3d35c70f742934b8091626f844416baf2855199964dc58
         * If you concatenated both hashes, using the web https://emn178.github.io/online-tools/sha256.html,
         * you will get the hashLink of the new AuditRecord ;)
         */
        // Arrange
        String expectedAuditRecordHash = "8008569e61c6bea20c3d35c70f742934b8091626f844416baf2855199964dc58";
        String expectedAuditRecordHashLink = "f09ba1b7cd0782e43e7f85ba54c81c1e9a27ebee1e51411e14b0790c025c48e0";
        // Get the most recent AuditRecord from the database
        AuditRecord auditRecordFound = auditRecordRepository.findMostRecentAuditRecord().block();
        System.out.println("auditRecordFound: " + auditRecordFound);
        assert auditRecordFound != null;
        // Calculate the hash and the hashlink of the AuditRecordRoot and set them
        String auditRecordHash = calculateSHA256(objectMapper.writeValueAsString(auditRecord));
        auditRecord.setHash(auditRecordHash);
        String auditRecordHashLink = calculateHashLink(auditRecordFound.getHashLink(), auditRecordHash);
        auditRecord.setHashLink(auditRecordHashLink);
        // Save AuditRecord
        AuditRecord auditRecordMono = auditRecordRepository.save(auditRecord).block();
        System.out.println(auditRecordMono);
        assert auditRecordMono != null;
        // The hash of the AuditRecord is the hash expected and calculated previously
        assertEquals(expectedAuditRecordHash, auditRecordMono.getHash());
        assertEquals(expectedAuditRecordHashLink, auditRecordMono.getHashLink());
    }

    @Order(2)
    @Test
    void shouldRetrieveAllAuditRecords() {
        Flux<AuditRecord> auditRecordFlux = auditRecordRepository.findAll();
        System.out.println(auditRecordFlux);
        StepVerifier.create(auditRecordFlux)
                .expectNextCount(2L)
                .verifyComplete();
    }

    @Order(3)
    @Test
    void shouldFindAuditRecordByEntityId() {
        Flux<AuditRecord> auditRecordMono =
                auditRecordRepository.findByEntityId("urn:ngsi-ld:ProductOffering:6e00d349-4c49-4bbe-83a9-65115f144908");
        StepVerifier.create(auditRecordMono)
                .assertNext(auditRecordMonoFound ->
                        assertEquals(auditRecord.getProcessId(), auditRecordMonoFound.getProcessId()))
                .verifyComplete();
    }

    @Order(4)
    @Test
    void shouldFindLatestPublishedTransactionByEntityId() {
        auditRecord.setStatus(AuditRecordStatus.PUBLISHED);
        auditRecord.setNewTransaction(false);
        auditRecordRepository.save(auditRecord).block();
        AuditRecord auditRecordMono = auditRecordRepository
                .findMostRecentPublishedOrDeletedByEntityId("urn:ngsi-ld:ProductOffering:6e00d349-4c49-4bbe-83a9-65115f144908")
                .block();
        assert auditRecordMono != null;
        assertEquals(auditRecord.getProcessId(), auditRecordMono.getProcessId());
    }

    @Order(5)
    @Test
    void shouldFindLatestDeletedTransactionByEntityId() {
        auditRecord.setStatus(AuditRecordStatus.DELETED);
        auditRecord.setNewTransaction(false);
        auditRecordRepository.save(auditRecord).block();
        AuditRecord auditRecordMono = auditRecordRepository
                .findMostRecentPublishedOrDeletedByEntityId("urn:ngsi-ld:ProductOffering:6e00d349-4c49-4bbe-83a9-65115f144908")
                .block();
        assert auditRecordMono != null;
        assertEquals(auditRecord.getProcessId(), auditRecordMono.getProcessId());
        // In the database, we have a transaction which status is DELETED and PUBLISHED, the last one is DELETED (@Order(5))
        assertEquals(AuditRecordStatus.DELETED, auditRecordMono.getStatus());
    }

    @Order(6)
    @Test
    void shouldVerifyLatestTransactionIsNotPublished() {
        AuditRecord auditRecordMono = auditRecordRepository
                .findMostRecentPublishedOrDeletedByEntityId("urn:ngsi-ld:ProductOffering:6e00d349-4c49-4bbe-83a9-65115f144908")
                .block();
        assert auditRecordMono != null;
        // In the database, we have a transaction which status is DELETED and PUBLISHED, the last one is DELETED (@Order(5))
        assertNotEquals(AuditRecordStatus.PUBLISHED, auditRecordMono.getStatus());
        assertEquals(AuditRecordStatus.DELETED, auditRecordMono.getStatus());
    }

    @Order(7)
    @Test
    void shouldDeleteAllTransactions() {
        Mono<Void> auditRecordMono = auditRecordRepository.deleteAll();
        StepVerifier.create(auditRecordMono)
                .verifyComplete();
    }

}
