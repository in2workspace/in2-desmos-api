package es.in2.desmos.domain.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrokerNotificationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testBuilderAndAccessors() {
        Map<String, Object> dataEntry = new HashMap<>();
        dataEntry.put("key1", "value1");
        dataEntry.put("key2", 42);
        List<Map<String, Object>> data = Arrays.asList(dataEntry);

        BrokerNotification notification = BrokerNotification.builder()
                .id("123")
                .type("TestType")
                .data(data)
                .subscriptionId("Subs456")
                .notifiedAt("2024-01-01T12:00:00Z")
                .build();

        assertEquals("123", notification.id());
        assertEquals("TestType", notification.type());
        assertEquals(data, notification.data());
        assertEquals("Subs456", notification.subscriptionId());
        assertEquals("2024-01-01T12:00:00Z", notification.notifiedAt());
    }

    @Test
    void testSerialization() throws Exception {
        Map<String, Object> dataEntry = new HashMap<>();
        dataEntry.put("key1", "value1");
        List<Map<String, Object>> data = Arrays.asList(dataEntry);

        BrokerNotification notification = BrokerNotification.builder()
                .id("123")
                .data(data)
                .build();

        String json = objectMapper.writeValueAsString(notification);
        assertTrue(json.contains("\"id\":\"123\""));
        assertTrue(json.contains("\"data\":[{\"key1\":\"value1\"}]"));
    }

    @Test
    void testDeserialization() throws Exception {
        String json = "{\"id\":\"123\",\"data\":[{\"key1\":\"value1\"}]}";
        BrokerNotification notification = objectMapper.readValue(json, BrokerNotification.class);

        assertEquals("123", notification.id());
        assertEquals("value1", notification.data().get(0).get("key1"));
    }

    @Test
    void testBrokerNotificationBuilderToString() {
        // Arrange
        String id = "123";
        String type = "type";
        List<Map<String, Object>> data = List.of(Collections.singletonMap("key", "value"));
        String subscriptionId = "subscriptionId";
        String notifiedAt = "notifiedAt";

        String expectedToString = "BrokerNotification.BrokerNotificationBuilder(id=" + id
                + ", type=" + type
                + ", data=" + data
                + ", subscriptionId=" + subscriptionId
                + ", notifiedAt=" + notifiedAt + ")";

        // Act
        BrokerNotification.BrokerNotificationBuilder brokerNotificationBuilder = BrokerNotification.builder()
                .id(id)
                .type(type)
                .data(data)
                .subscriptionId(subscriptionId)
                .notifiedAt(notifiedAt);

        // Assert
        assertEquals(expectedToString, brokerNotificationBuilder.toString());
    }


}
