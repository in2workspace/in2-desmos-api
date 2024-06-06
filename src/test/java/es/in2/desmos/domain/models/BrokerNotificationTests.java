package es.in2.desmos.domain.models;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrokerNotificationTests {

    private final String id = "1234";
    private final String type = "TestType";
    private final List<Map<String, Object>> data = List.of(Collections.singletonMap("key", "value"));
    private final String subscriptionId = "urn:ngsi-ld:Subscription:7230a0ac-ed33-44e4-bb04-ce0f345b9d2a";
    private final String notifiedAt = "2024-01-01T12:00:00Z";

    @Test
    void testBuilderAndLombokGeneratedMethods() {
        BrokerNotification notification = BrokerNotification.builder()
                .id(id)
                .type(type)
                .data(data)
                .subscriptionId(subscriptionId)
                .notifiedAt(notifiedAt)
                .build();
        assertEquals(id, notification.id());
        assertEquals(type, notification.type());
        assertEquals(data, notification.data());
        assertEquals(subscriptionId, notification.subscriptionId());
        assertEquals(notifiedAt, notification.notifiedAt());
    }

    @Test
    void testToString() {
        // Arrange
        BrokerNotification brokerNotification = BrokerNotification.builder()
                .id(id)
                .type(type)
                .data(data)
                .subscriptionId(subscriptionId)
                .notifiedAt(notifiedAt)
                .build();
        // Act
        String result = brokerNotification.toString();
        // Assert
        assertTrue(result.contains(id));
        assertTrue(result.contains(type));
        assertTrue(result.contains(data.toString()));
        assertTrue(result.contains(subscriptionId));
        assertTrue(result.contains(notifiedAt));
    }

    @Test
    void testBlockchainTxPayloadBuilderToString() {
        // Arrange
        String expectedToString = "BrokerNotification.BrokerNotificationBuilder(id=" + id
                + ", type=" + type
                + ", data=" + data
                + ", subscriptionId=" + subscriptionId
                + ", notifiedAt=" + notifiedAt + ")";
        // Act
        BrokerNotification.BrokerNotificationBuilder blockchainTxPayloadBuilder = BrokerNotification.builder()
                .id(id)
                .type(type)
                .data(data)
                .subscriptionId(subscriptionId)
                .notifiedAt(notifiedAt);
        // Assert
        assertEquals(expectedToString, blockchainTxPayloadBuilder.toString());
    }

}