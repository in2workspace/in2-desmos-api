package es.in2.desmos.domain.models;


import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuditRecordTests {

    // Test data set
    private final UUID id = UUID.randomUUID();
    private final String processId = "e531db3a-1931-4ded-8c37-d2e8235106a1";
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());
    private final String entityId = "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69";
    private final String entityType = "ProductOffering";
    private final String entityHash = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4"; //1234
    private final String entityHashLink = "dbff5341acad5e2a58db4efd5e72e2d9a0a843a28e02b1183c68162d0a3a3de6"; //9876
    private final String dataLocation = "http://localhost:8080/ngsi-ld/v1/entities/" + entityId + "?hl=" + entityHashLink;
    private final AuditRecordStatus status = AuditRecordStatus.CREATED;
    private final AuditRecordTrader trader = AuditRecordTrader.PRODUCER;
    private final String hash = "";
    private final String hashLink = "";
    private final boolean newTransaction = true;


    @Test
    void testNoArgsConstructor() {
        // Act
        AuditRecord auditRecord = new AuditRecord();
        // Assert
        assertNull(auditRecord.getId());
        assertNull(auditRecord.getProcessId());
        assertNull(auditRecord.getCreatedAt());
        assertNull(auditRecord.getEntityId());
        assertNull(auditRecord.getEntityType());
        assertNull(auditRecord.getEntityHash());
        assertNull(auditRecord.getStatus());
        assertNull(auditRecord.getTrader());
        assertNull(auditRecord.getHash());
        assertTrue(auditRecord.isNew());
    }

    @Test
    void testBuilderAndLombokGeneratedMethods() {
        // Act
        AuditRecord auditRecord = AuditRecord.builder()
                .id(id)
                .processId(processId)
                .createdAt(createdAt)
                .entityId(entityId)
                .entityType(entityType)
                .entityHash(entityHash)
                .status(status)
                .trader(trader)
                .hash(hash)
                .dataLocation(dataLocation)
                .entityHashLink(entityHashLink)
                .hashLink(hashLink)
                .newTransaction(newTransaction)
                .build();
        // Assert
        assertEquals(id, auditRecord.getId());
        assertEquals(processId, auditRecord.getProcessId());
        assertEquals(createdAt, auditRecord.getCreatedAt());
        assertEquals(entityId, auditRecord.getEntityId());
        assertEquals(entityType, auditRecord.getEntityType());
        assertEquals(entityHash, auditRecord.getEntityHash());
        assertEquals(status, auditRecord.getStatus());
        assertEquals(trader, auditRecord.getTrader());
        assertEquals(hash, auditRecord.getHash());
        assertEquals(dataLocation, auditRecord.getDataLocation());
        assertEquals(entityHashLink, auditRecord.getEntityHashLink());
        assertEquals(hashLink, auditRecord.getHashLink());
        assertTrue(auditRecord.isNew());
    }

    @Test
    void testSettersAndLombokGeneratedMethods() {
        // Act
        AuditRecord auditRecord = new AuditRecord();
        auditRecord.setId(id);
        auditRecord.setProcessId(processId);
        auditRecord.setCreatedAt(createdAt);
        auditRecord.setEntityId(entityId);
        auditRecord.setEntityType(entityType);
        auditRecord.setEntityHash(entityHash);
        auditRecord.setStatus(status);
        auditRecord.setTrader(trader);
        auditRecord.setHash(hash);
        auditRecord.setDataLocation(dataLocation);
        auditRecord.setEntityHashLink(entityHashLink);
        auditRecord.setHashLink(hashLink);
        auditRecord.setNewTransaction(newTransaction);
        // Assert
        assertEquals(id, auditRecord.getId());
        assertEquals(processId, auditRecord.getProcessId());
        assertEquals(createdAt, auditRecord.getCreatedAt());
        assertEquals(entityId, auditRecord.getEntityId());
        assertEquals(entityType, auditRecord.getEntityType());
        assertEquals(entityHash, auditRecord.getEntityHash());
        assertEquals(status, auditRecord.getStatus());
        assertEquals(trader, auditRecord.getTrader());
        assertEquals(hash, auditRecord.getHash());
        assertEquals(dataLocation, auditRecord.getDataLocation());
        assertEquals(entityHashLink, auditRecord.getEntityHashLink());
        assertEquals(hashLink, auditRecord.getHashLink());
        assertTrue(auditRecord.isNew());
    }

    @Test
    void testIsNew() {
        // Arrange
        AuditRecord auditRecord = new AuditRecord();
        auditRecord.setId(id);
        auditRecord.setNewTransaction(false);
        // Assert not new because ID is set and newTransaction is false
        assertFalse(auditRecord.isNew());
        // Arrange with ID null
        auditRecord.setId(null);
        // Assert new because ID is null
        assertTrue(auditRecord.isNew());
        // Arrange with newTransaction true
        auditRecord.setNewTransaction(true);
        // Assert new because newTransaction is true
        assertTrue(auditRecord.isNew());
    }

    @Test
    void testToString() {
        // Arrange
        AuditRecord auditRecord = AuditRecord.builder()
                .id(id)
                .processId(processId)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .entityId(entityId)
                .entityType(entityType)
                .entityHash(entityHash)
                .entityHashLink(entityHashLink)
                .dataLocation(dataLocation)
                .status(status)
                .trader(trader)
                .hash(hash)
                .hashLink(hashLink)
                .newTransaction(true)
                .build();
        // Act
        String result = auditRecord.toString();
        // Assert
        assertTrue(result.contains(id.toString()));
        assertTrue(result.contains(processId));
        assertTrue(result.contains(entityId));
        assertTrue(result.contains(entityType));
        assertTrue(result.contains(entityHash));
        assertTrue(result.contains(entityHashLink));
        assertTrue(result.contains(dataLocation));
        assertTrue(result.contains(status.toString()));
        assertTrue(result.contains(trader.toString()));
        assertTrue(result.contains("true"));
    }

    @Test
    void testAuditRecordBuilderToString() {
        // Arrange
        String expectedToString = "AuditRecord.AuditRecordBuilder(id=" + id
                + ", processId=" + processId
                + ", createdAt=" + createdAt
                + ", entityId=" + entityId
                + ", entityType=" + entityType
                + ", entityHash=" + entityHash
                + ", entityHashLink=" + entityHashLink
                + ", dataLocation=" + dataLocation
                + ", status=" + status
                + ", trader=" + trader
                + ", hash=" + hash
                + ", hashLink=" + hashLink
                + ", newTransaction=" + newTransaction + ")";
        // Act
        AuditRecord.AuditRecordBuilder auditRecordBuilder = AuditRecord.builder()
                .id(id)
                .processId(processId)
                .createdAt(createdAt)
                .entityId(entityId)
                .entityType(entityType)
                .entityHash(entityHash)
                .entityHashLink(entityHashLink)
                .dataLocation(dataLocation)
                .status(status)
                .trader(trader)
                .hash(hash)
                .hashLink(hashLink)
                .newTransaction(newTransaction);
        // Assert
        assertEquals(expectedToString, auditRecordBuilder.toString());
    }

}