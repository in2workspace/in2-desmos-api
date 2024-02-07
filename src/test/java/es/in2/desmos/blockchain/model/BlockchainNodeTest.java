package es.in2.desmos.blockchain.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockchainNodeTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testBuilderAndAccessors() {
        // Arrange
        String userEthereumAddress = "0x12345";
        String organizationId = "org123";
        // Act
        BlockchainNode blockchainNode = BlockchainNode.builder()
                .userEthereumAddress(userEthereumAddress)
                .organizationId(organizationId)
                .build();
        // Assert
        assertEquals(userEthereumAddress, blockchainNode.userEthereumAddress());
        assertEquals(organizationId, blockchainNode.organizationId());
    }

    @Test
    void testSerialization() throws Exception {
        // Arrange
        BlockchainNode blockchainNode = BlockchainNode.builder()
                .userEthereumAddress("0x12345")
                .organizationId("org123")
                .build();
        // Act
        String json = objectMapper.writeValueAsString(blockchainNode);
        // Assert
        assertTrue(json.contains("\"userEthereumAddress\":\"0x12345\""));
        assertTrue(json.contains("\"iss\":\"org123\""));
    }

    @Test
    void testDeserialization() throws Exception {
        // Arrange
        String json = "{\"userEthereumAddress\":\"0x12345\",\"iss\":\"org123\"}";
        // Act
        BlockchainNode blockchainNode = objectMapper.readValue(json, BlockchainNode.class);
        // Assert
        assertEquals("0x12345", blockchainNode.userEthereumAddress());
        assertEquals("org123", blockchainNode.organizationId());
    }
}