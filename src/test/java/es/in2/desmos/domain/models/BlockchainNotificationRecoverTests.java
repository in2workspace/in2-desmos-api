package es.in2.desmos.domain.models;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockchainNotificationRecoverTests {

    @Test
    void testBuilderAndLombokGeneratedMethods() {
        // Arrange
        UUID id = UUID.randomUUID();
        String processId = "process-123";
        String notificationId = "notification-456";
        String publisherAddress = "https://example.com";
        String eventType = "Event-Type";
        long timestamp = System.currentTimeMillis();
        String dataLocation = "https://example.com/data";
        String relevantMetadata = "metadata-info";
        String entityIdHash = "entity-hash";
        String previousEntityHash = "previous-hash";
        String eventQueuePriority = "HIGH";

        // Act
        BlockchainNotificationRecover notificationRecover = BlockchainNotificationRecover.builder()
                .id(id)
                .processId(processId)
                .notificationId(notificationId)
                .publisherAddress(publisherAddress)
                .eventType(eventType)
                .timestamp(timestamp)
                .dataLocation(dataLocation)
                .relevantMetadata(relevantMetadata)
                .entityIdHash(entityIdHash)
                .previousEntityHash(previousEntityHash)
                .eventQueuePriority(eventQueuePriority)
                .build();

        // Assert
        assertEquals(id, notificationRecover.getId());
        assertEquals(processId, notificationRecover.getProcessId());
        assertEquals(notificationId, notificationRecover.getNotificationId());
        assertEquals(publisherAddress, notificationRecover.getPublisherAddress());
        assertEquals(eventType, notificationRecover.getEventType());
        assertEquals(timestamp, notificationRecover.getTimestamp());
        assertEquals(dataLocation, notificationRecover.getDataLocation());
        assertEquals(relevantMetadata, notificationRecover.getRelevantMetadata());
        assertEquals(entityIdHash, notificationRecover.getEntityIdHash());
        assertEquals(previousEntityHash, notificationRecover.getPreviousEntityHash());
        assertEquals(eventQueuePriority, notificationRecover.getEventQueuePriority());
    }

    @Test
    void testIsNew() {
        // Arrange
        BlockchainNotificationRecover notificationRecover = BlockchainNotificationRecover.builder()
                .id(UUID.randomUUID())
                .newBlockchainNotificationRecover(true)
                .build();

        // Act & Assert
        assertTrue(notificationRecover.isNew());
    }

    @Test
    void testToString() {
        // Arrange
        UUID id = UUID.randomUUID();
        BlockchainNotificationRecover notificationRecover = BlockchainNotificationRecover.builder()
                .id(id)
                .processId("process-123")
                .build();

        // Act
        String result = notificationRecover.toString();

        // Assert
        assertTrue(result.contains("id=" + id));
        assertTrue(result.contains("processId=process-123"));
    }
}
