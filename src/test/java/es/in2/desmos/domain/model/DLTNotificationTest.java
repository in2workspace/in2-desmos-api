package es.in2.desmos.domain.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DLTNotificationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testBuilderAndAccessors() {
        List<String> metadata = Arrays.asList("meta1", "meta2");

        DLTNotification notification = DLTNotification.builder()
                .id(5478474)
                .publisherAddress("Address789")
                .eventType("Event012")
                .timestamp(684485648)
                .dataLocation("Location345")
                .relevantMetadata(metadata)
                .build();

        assertEquals(5478474, notification.id());
        assertEquals("Address789", notification.publisherAddress());
        assertEquals("Event012", notification.eventType());
        assertEquals(684485648, notification.timestamp());
        assertEquals("Location345", notification.dataLocation());
        assertEquals(metadata, notification.relevantMetadata());
    }

    @Test
    void testImmutability() {
        List<String> metadata = Arrays.asList("meta1", "meta2");
        DLTNotification notification = DLTNotification.builder()
                .relevantMetadata(metadata)
                .build();
        List<String> eventMetadata = notification.relevantMetadata();
        Assert.assertThrows(UnsupportedOperationException.class, () -> eventMetadata.add("meta3"));
    }

    @Test
    void testSerialization() throws Exception {
        DLTNotification notification = DLTNotification.builder()
                .id(5478474)
                .publisherAddress("Address789")
                .build();
        String json = objectMapper.writeValueAsString(notification);
        System.out.println(json);
        assertTrue(json.replaceAll("\\s", "")
                .contains("\"id\":5478474"));
        assertTrue(json.contains("\"publisherAddress\":\"Address789\""));
    }


    @Test
    void testDeserialization() throws Exception {
        String json = "{\"id\": 5478474, \"publisherAddress\":\"Address789\"}";
        DLTNotification notification = objectMapper.readValue(json, DLTNotification.class);

        assertEquals(5478474, notification.id());
        assertEquals("Address789", notification.publisherAddress());
    }

    @Test
    void testSetEntityId() {
        // Arrange
        DLTNotification.DLTNotificationBuilder dltNotificationBuilder = DLTNotification.builder();

        String entityId = "entityId";

        // Act
        dltNotificationBuilder.entityId(entityId);

        // Assert
        assertEquals(entityId, dltNotificationBuilder.build().entityId(), "Expected the entityId to be set correctly");
    }

    @Test
    void testSetPreviousEntityHash() {
        // Arrange
        DLTNotification.DLTNotificationBuilder blockchainNotificationBuilder = DLTNotification.builder();
        String previousEntityHash = "previousEntityHash";

        // Act
        blockchainNotificationBuilder.previousEntityHash(previousEntityHash);

        // Assert
        assertEquals(previousEntityHash, blockchainNotificationBuilder.build().previousEntityHash(), "Expected the " +
                "previousEntityHash to be set correctly");
    }

    @Test
    void testBlockchainNotificationBuilderToString() {
        // Arrange
        long id = 123L;
        String publisherAddress = "publisherAddress";
        String eventType = "eventType";
        long timestamp = System.currentTimeMillis();
        String dataLocation = "dataLocation";
        List<String> relevantMetadata = Arrays.asList("metadata1", "metadata2");
        String entityId = "entityId";
        String previousEntityHash = "previousEntityHash";

        String expectedToString = "DLTNotification.DLTNotificationBuilder(id=" + id
                + ", publisherAddress=" + publisherAddress
                + ", eventType=" + eventType
                + ", timestamp=" + timestamp
                + ", dataLocation=" + dataLocation
                + ", relevantMetadata=" + relevantMetadata
                + ", entityId=" + entityId
                + ", previousEntityHash=" + previousEntityHash + ")";

        // Act
        DLTNotification.DLTNotificationBuilder blockchainNotificationBuilder = DLTNotification.builder()
                .id(id)
                .publisherAddress(publisherAddress)
                .eventType(eventType)
                .timestamp(timestamp)
                .dataLocation(dataLocation)
                .relevantMetadata(relevantMetadata)
                .entityId(entityId)
                .previousEntityHash(previousEntityHash);

        // Assert
        assertEquals(expectedToString, blockchainNotificationBuilder.toString());
    }


}