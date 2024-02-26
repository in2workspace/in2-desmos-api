package es.in2.desmos.blockchain.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DLTAdapterSubscriptionTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testBuilderAndAccessors() {
        // Arrange
        List<String> eventTypes = List.of("type1", "type2");
        String notificationEndpoint = "http://example.com/notify";
        // Act
        DLTAdapterSubscription subscription = DLTAdapterSubscription.builder()
                .eventTypes(eventTypes)
                .notificationEndpoint(notificationEndpoint)
                .build();
        // Assert
        assertEquals(eventTypes, subscription.eventTypes());
        assertEquals(notificationEndpoint, subscription.notificationEndpoint());
    }

    @Test
    void testSerialization() throws Exception {
        // Arrange
        DLTAdapterSubscription subscription = DLTAdapterSubscription.builder()
                .eventTypes(List.of("type1", "type2"))
                .notificationEndpoint("http://example.com/notify")
                .build();
        // Act
        String json = objectMapper.writeValueAsString(subscription);
        // Assert
        assertTrue(json.contains("\"eventTypes\":[\"type1\",\"type2\"]"));
        assertTrue(json.contains("\"notificationEndpoint\":\"http://example.com/notify\""));
    }

    @Test
    void testDeserialization() throws Exception {
        // Arrange
        String json = "{\"eventTypes\":[\"type1\",\"type2\"],\"notificationEndpoint\":\"http://example.com/notify\"}";
        // Act
        DLTAdapterSubscription subscription = objectMapper.readValue(json, DLTAdapterSubscription.class);
        // Assert
        assertEquals(List.of("type1", "type2"), subscription.eventTypes());
        assertEquals("http://example.com/notify", subscription.notificationEndpoint());
    }

}
