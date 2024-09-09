package es.in2.desmos.domain.models;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockchainTxPayloadTests {

    private final String eventType = "ProductOffering";
    private final String organizationIdentifier = "0x40b0ab9dfd960064fb7e9fdf77f889c71569e349055ff563e8d699d8fa97fa90";
    private final String entityId = "0x6f6468ded8276d009ab1b6c578c2b922053acd6b5a507f36d408d3f7c9ae91d0";
    private final String previousEntityHash = "0x98d9658d98764dbe135b316f52a98116b4b02f9d7e57212aa86335c42a58539a";
    private final String dataLocation = "http://localhost:8080/ngsi-ld/v1/entities/" +
            "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69" +
            "?hl=03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
    private final List<String> metadata = List.of("0xdd98910dbc7831753bab3da302ce5bf9d73ac13961913c2e774de8e737867f0d",
            "0x947cccb1a978e374a4b36550389768d405bf5b81817175ab9023b5e3d96ab966");

    @Test
    void testBuilderAndLombokGeneratedMethods() {
        // Act
        BlockchainTxPayload blockchainTxPayload = BlockchainTxPayload.builder()
                .eventType(eventType)
                .organizationIdentifier(organizationIdentifier)
                .entityId(entityId)
                .previousEntityHashLink(previousEntityHash)
                .dataLocation(dataLocation)
                .metadata(metadata)
                .build();
        // Assert
        assertEquals(eventType, blockchainTxPayload.eventType());
        assertEquals(organizationIdentifier, blockchainTxPayload.organizationIdentifier());
        assertEquals(entityId, blockchainTxPayload.entityId());
        assertEquals(previousEntityHash, blockchainTxPayload.previousEntityHashLink());
        assertEquals(dataLocation, blockchainTxPayload.dataLocation());
        assertEquals(metadata, blockchainTxPayload.metadata());
    }

    @Test
    void testImmutability() {
        BlockchainTxPayload blockchainTxPayload = BlockchainTxPayload.builder()
                .metadata(metadata)
                .build();
        List<String> metadataList = blockchainTxPayload.metadata();
        Assert.assertThrows(UnsupportedOperationException.class, () -> metadataList.add("meta3"));
    }

    @Test
    void testToString() {
        // Arrange
        BlockchainTxPayload blockchainTxPayload = BlockchainTxPayload.builder()
                .eventType(eventType)
                .organizationIdentifier(organizationIdentifier)
                .entityId(entityId)
                .previousEntityHashLink(previousEntityHash)
                .dataLocation(dataLocation)
                .metadata(metadata)
                .build();
        // Act
        String result = blockchainTxPayload.toString();
        // Assert
        assertTrue(result.contains("eventType=" + eventType));
        assertTrue(result.contains("organizationIdentifier=" + organizationIdentifier));
        assertTrue(result.contains("entityId=" + entityId));
        assertTrue(result.contains("previousEntityHashLink=" + previousEntityHash));
        assertTrue(result.contains("dataLocation=" + dataLocation));
        assertTrue(result.contains("metadata=" + metadata));
    }

    @Test
    void testBlockchainTxPayloadBuilderToString() {
        // Arrange
        String expectedToString = "BlockchainTxPayload.BlockchainTxPayloadBuilder(" +
                "eventType=ProductOffering, " +
                "organizationIdentifier=0x40b0ab9dfd960064fb7e9fdf77f889c71569e349055ff563e8d699d8fa97fa90, " +
                "entityId=0x6f6468ded8276d009ab1b6c578c2b922053acd6b5a507f36d408d3f7c9ae91d0, " +
                "previousEntityHashLink=0x98d9658d98764dbe135b316f52a98116b4b02f9d7e57212aa86335c42a58539a, " +
                "dataLocation=http://localhost:8080/ngsi-ld/v1/entities/urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69?hl=03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4, " +
                "metadata=[0xdd98910dbc7831753bab3da302ce5bf9d73ac13961913c2e774de8e737867f0d, " +
                "0x947cccb1a978e374a4b36550389768d405bf5b81817175ab9023b5e3d96ab966]" +
                ")";
        // Act
        BlockchainTxPayload.BlockchainTxPayloadBuilder blockchainTxPayloadBuilder = BlockchainTxPayload.builder()
                .eventType(eventType)
                .organizationIdentifier(organizationIdentifier)
                .entityId(entityId)
                .previousEntityHashLink(previousEntityHash)
                .dataLocation(dataLocation)
                .metadata(metadata);
        // Assert
        assertEquals(expectedToString, blockchainTxPayloadBuilder.toString());
    }

}