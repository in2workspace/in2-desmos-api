package es.in2.desmos.domain.models;

import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockchainTxPayloadRecoverTests {

    @Test
    void testBuilderAndLombokGeneratedMethods() {
        // Arrange
        UUID id = UUID.randomUUID();
        String processId = "process-123";
        String eventType = "TransactionCompleted";
        String organizationId = "org-456";
        String entityId = "entity-789";
        String previousEntityHash = "hash-abc";
        String dataLocation = "http://example.com/data";
        String relevantMetadata = "metadata-important";
        String eventQueuePriority = "HIGH";

        // Act
        BlockchainTxPayloadRecover txPayloadRecover = BlockchainTxPayloadRecover.builder()
                .id(id)
                .processId(processId)
                .eventType(eventType)
                .organizationId(organizationId)
                .entityId(entityId)
                .previousEntityHash(previousEntityHash)
                .dataLocation(dataLocation)
                .relevantMetadata(relevantMetadata)
                .eventQueuePriority(eventQueuePriority)
                .build();

        // Assert
        assertEquals(id, txPayloadRecover.getId());
        assertEquals(processId, txPayloadRecover.getProcessId());
        assertEquals(eventType, txPayloadRecover.getEventType());
        assertEquals(organizationId, txPayloadRecover.getOrganizationId());
        assertEquals(entityId, txPayloadRecover.getEntityId());
        assertEquals(previousEntityHash, txPayloadRecover.getPreviousEntityHash());
        assertEquals(dataLocation, txPayloadRecover.getDataLocation());
        assertEquals(relevantMetadata, txPayloadRecover.getRelevantMetadata());
        assertEquals(eventQueuePriority, txPayloadRecover.getEventQueuePriority());
    }

    @Test
    void testIsNew() {
        // Arrange
        BlockchainTxPayloadRecover txPayloadRecover = new BlockchainTxPayloadRecover();
        txPayloadRecover.setNewBlockchainTxPayloadRecover(true);

        // Act & Assert
        assertTrue(txPayloadRecover.isNew(), "The entity should be considered 'new' if newTransaction is true.");
    }

    @Test
    void testToString() {
        // Arrange
        UUID id = UUID.randomUUID();
        BlockchainTxPayloadRecover txPayloadRecover = BlockchainTxPayloadRecover.builder()
                .id(id)
                .processId("process-123")
                .eventType("TransactionCompleted")
                .build();

        // Act
        String result = txPayloadRecover.toString();

        // Assert
        assertTrue(result.contains("id=" + id.toString()));
        assertTrue(result.contains("processId=process-123"));
        assertTrue(result.contains("eventType=TransactionCompleted"));
    }
}
