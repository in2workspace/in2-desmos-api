package es.in2.desmos.api.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlockchainEventTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testBuilderAndAccessors() {
        List<String> metadata = Arrays.asList("meta1", "meta2");
        BlockchainEvent event = BlockchainEvent.builder()
                .eventType("TestType")
                .organizationId("Org123")
                .entityId("Entity456")
                .previousEntityHash("Hash789")
                .dataLocation("Location012")
                .metadata(metadata)
                .build();

        assertEquals("TestType", event.eventType());
        assertEquals("Org123", event.organizationId());
        assertEquals("Entity456", event.entityId());
        assertEquals("Hash789", event.previousEntityHash());
        assertEquals("Location012", event.dataLocation());
        assertEquals(metadata, event.metadata());
    }

    @Test
    void testImmutability() {
        List<String> metadata = Arrays.asList("meta1", "meta2");
        BlockchainEvent event = BlockchainEvent.builder()
                .eventType("TestType")
                .organizationId("Org123")
                .metadata(metadata)
                .build();

        List<String> eventMetadata = event.metadata();
        Assert.assertThrows(UnsupportedOperationException.class, () -> eventMetadata.add("meta3"));
    }

    @Test
    void testSerialization() throws Exception {
        BlockchainEvent event = BlockchainEvent.builder()
                .eventType("TestType")
                .organizationId("Org123")
                .build();

        String json = objectMapper.writeValueAsString(event);
        assertTrue(json.contains("\"eventType\":\"TestType\""));
        assertTrue(json.contains("\"iss\":\"Org123\""));
    }

    @Test
    void testDeserialization() throws Exception {
        String json = "{\"eventType\":\"TestType\",\"iss\":\"Org123\"}";
        BlockchainEvent event = objectMapper.readValue(json, BlockchainEvent.class);

        assertEquals("TestType", event.eventType());
        assertEquals("Org123", event.organizationId());
    }

}

