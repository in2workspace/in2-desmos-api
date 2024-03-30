package es.in2.desmos.domain.models;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockchainNotificationTest {

    // Test data set
    private final long id = 1234;
    private final String publisherAddress = "http://blockchain-testnode.infra.svc.cluster.local:8545/";
    private final String eventType = "ProductOffering";
    private final long timestamp = 1711801566;
    private final String dataLocation = "http://localhost:8080/ngsi-ld/v1/entities/" +
            "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69" +
            "?hl=03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
    private final List<String> relevantMetadata = List.of("metadata1", "metadata2");
    private final String entityIdHash = "6f6468ded8276d009ab1b6c578c2b922053acd6b5a507f36d408d3f7c9ae91d0";
    private final String previousEntityHash = "98d9658d98764dbe135b316f52a98116b4b02f9d7e57212aa86335c42a58539a"; //7283

    @Test
    void testBuilderAndLombokGeneratedMethods() {
        // Act
        BlockchainNotification blockchainNotification = BlockchainNotification.builder()
                .id(id)
                .publisherAddress(publisherAddress)
                .eventType(eventType)
                .timestamp(timestamp)
                .dataLocation(dataLocation)
                .relevantMetadata(relevantMetadata)
                .entityId(entityIdHash)
                .previousEntityHash(previousEntityHash)
                .build();
        // Assert
        assertEquals(id, blockchainNotification.id());
        assertEquals(publisherAddress, blockchainNotification.publisherAddress());
        assertEquals(eventType, blockchainNotification.eventType());
        assertEquals(timestamp, blockchainNotification.timestamp());
        assertEquals(dataLocation, blockchainNotification.dataLocation());
        assertEquals(relevantMetadata, blockchainNotification.relevantMetadata());
        assertEquals(entityIdHash, blockchainNotification.entityId());
        assertEquals(previousEntityHash, blockchainNotification.previousEntityHash());
    }

    @Test
    void testImmutability() {
        BlockchainNotification notification = BlockchainNotification.builder()
                .relevantMetadata(relevantMetadata)
                .build();
        List<String> eventMetadata = notification.relevantMetadata();
        Assert.assertThrows(UnsupportedOperationException.class, () -> eventMetadata.add("meta3"));
    }


    @Test
    void testToString() {
        // Arrange
        BlockchainNotification blockchainNotification = BlockchainNotification.builder()
                .id(id)
                .publisherAddress(publisherAddress)
                .eventType(eventType)
                .timestamp(timestamp)
                .dataLocation(dataLocation)
                .relevantMetadata(relevantMetadata)
                .entityId(entityIdHash)
                .previousEntityHash(previousEntityHash)
                .build();
        // Act
        String result = blockchainNotification.toString();
        // Assert
        assertTrue(result.contains("id=" + id));
        assertTrue(result.contains("publisherAddress=" + publisherAddress));
        assertTrue(result.contains("eventType=" + eventType));
        assertTrue(result.contains("timestamp=" + timestamp));
        assertTrue(result.contains("dataLocation=" + dataLocation));
        assertTrue(result.contains("relevantMetadata=" + relevantMetadata));
        assertTrue(result.contains("entityId=" + entityIdHash));
        assertTrue(result.contains("previousEntityHash=" + previousEntityHash));
    }

    @Test
    void testBlockchainNotificationBuilderToString() {
        // Arrange
        String expectedToString = "BlockchainNotification.BlockchainNotificationBuilder(id=" + id
                + ", publisherAddress=" + publisherAddress
                + ", eventType=" + eventType
                + ", timestamp=" + timestamp
                + ", dataLocation=" + dataLocation
                + ", relevantMetadata=" + relevantMetadata
                + ", entityId=" + entityIdHash
                + ", previousEntityHash=" + previousEntityHash + ")";
        // Act
        BlockchainNotification.BlockchainNotificationBuilder blockchainNotificationBuilder = BlockchainNotification.builder()
                .id(id)
                .publisherAddress(publisherAddress)
                .eventType(eventType)
                .timestamp(timestamp)
                .dataLocation(dataLocation)
                .relevantMetadata(relevantMetadata)
                .entityId(entityIdHash)
                .previousEntityHash(previousEntityHash);
        // Assert
        assertEquals(expectedToString, blockchainNotificationBuilder.toString());
    }

}
