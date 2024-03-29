package es.in2.desmos.domain.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DLTEventTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Data for testing
    private final String entityType = "ProductOffering";
    private final String organizationId = "ec22a787d8...3365894b3c";
    private final String entityId = "urn:ngsi-ld:ProductOffering:12345678";
    private final String previousEntityHash = "03ac674216...59e13f978d";
    private final String dataLocation = "https://example.com/ngsi-ld/v1/entities/urn:ngsi-ld:ProductOffering:12345678?hl=03ac674216...78d7c846f4";
    private final List<String> metadata = Arrays.asList("metadata1", "metadata2");


    @Test
    void testBuilderAndAccessors() {
        DLTEvent event = DLTEvent.builder()
                .eventType(entityType)
                .organizationId(organizationId)
                .entityId(entityId)
                .previousEntityHash(previousEntityHash)
                .dataLocation(dataLocation)
                .metadata(metadata)
                .build();
        assertEquals("ProductOffering", event.eventType());
        assertEquals("ec22a787d8...3365894b3c", event.organizationId());
        assertEquals("urn:ngsi-ld:ProductOffering:12345678", event.entityId());
        assertEquals("03ac674216...59e13f978d", event.previousEntityHash());
        assertEquals("https://example.com/ngsi-ld/v1/entities/urn:ngsi-ld:ProductOffering:12345678?hl=03ac674216...78d7c846f4", event.dataLocation());
        assertEquals(metadata, event.metadata());
    }

    @Test
    void testImmutability() {
        DLTEvent event = DLTEvent.builder()
                .eventType(entityType)
                .organizationId(organizationId)
                .metadata(metadata)
                .build();
        List<String> eventMetadata = event.metadata();
        Assert.assertThrows(UnsupportedOperationException.class, () -> eventMetadata.add("meta3"));
    }

    @Test
    void testSerialization() throws Exception {
        DLTEvent event = DLTEvent.builder()
                .eventType(entityType)
                .organizationId(organizationId)
                .build();
        String json = objectMapper.writeValueAsString(event);
        assertTrue(json.contains("\"eventType\":\"ProductOffering\""));
        assertTrue(json.contains("\"iss\":\"ec22a787d8...3365894b3c\""));
    }

    @Test
    void testDeserialization() throws Exception {
        String json = "{\"eventType\":\"ProductOffering\",\"iss\":\"ec22a787d8...3365894b3c\"}";
        DLTEvent event = objectMapper.readValue(json, DLTEvent.class);
        assertEquals("ProductOffering", event.eventType());
        assertEquals("ec22a787d8...3365894b3c", event.organizationId());
    }

    @Test
    void testDLTEventBuilderToString() {
        // Arrange
        String expectedToString = "DLTEvent.DLTEventBuilder(eventType=" + entityType
                + ", organizationId=" + organizationId
                + ", entityId=" + entityId
                + ", previousEntityHash=" + previousEntityHash
                + ", dataLocation=" + dataLocation
                + ", metadata=" + metadata + ")";
        // Act
        DLTEvent.DLTEventBuilder blockchainEventBuilder = DLTEvent.builder()
                .eventType(entityType)
                .organizationId(organizationId)
                .entityId(entityId)
                .previousEntityHash(previousEntityHash)
                .dataLocation(dataLocation)
                .metadata(metadata);
        // Assert
        assertEquals(expectedToString, blockchainEventBuilder.toString());
    }

}