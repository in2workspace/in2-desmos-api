package es.in2.desmos.domain.models;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DomeParticipantTests {

    // Test data set
    private final UUID id = UUID.randomUUID();
    private final String ethereumAddress = "0x1234567890abcdef";
    private final boolean newTransaction = true;

    @Test
    void testNoArgsConstructor() {
        // Act
        DomeParticipant domeParticipant = new DomeParticipant();
        // Assert
        assertNull(domeParticipant.getId());
        assertNull(domeParticipant.getEthereumAddress());
        assertTrue(domeParticipant.isNew());
    }

    @Test
    void testBuilderAndLombokGeneratedMethods() {
        // Act
        DomeParticipant domeParticipant = DomeParticipant.builder()
                .id(id)
                .ethereumAddress(ethereumAddress)
                .newTransaction(newTransaction)
                .build();
        // Assert
        assertEquals(id, domeParticipant.getId());
        assertEquals(ethereumAddress, domeParticipant.getEthereumAddress());
        assertTrue(domeParticipant.isNew());
    }

    @Test
    void testSettersAndLombokGeneratedMethods() {
        // Arrange
        DomeParticipant domeParticipant = new DomeParticipant();
        // Act
        domeParticipant.setId(id);
        domeParticipant.setEthereumAddress(ethereumAddress);
        domeParticipant.setNewTransaction(newTransaction);
        // Assert
        assertEquals(id, domeParticipant.getId());
        assertEquals(ethereumAddress, domeParticipant.getEthereumAddress());
        assertTrue(domeParticipant.isNew());
    }

    @Test
    void testIsNew() {
        // Arrange
        DomeParticipant domeParticipant = DomeParticipant.builder()
                .id(id)
                .ethereumAddress(ethereumAddress)
                .newTransaction(newTransaction)
                .build();
        // Act
        boolean result = domeParticipant.isNew();
        // Assert
        assertTrue(result);
    }

    @Test
    void testToString() {
        // Arrange
        String expectedToString = "DomeParticipant(id=" + id
                + ", ethereumAddress=" + ethereumAddress
                + ", newTransaction=" + newTransaction + ")";
        // Act
        DomeParticipant domeParticipant = DomeParticipant.builder()
                .id(id)
                .ethereumAddress(ethereumAddress)
                .newTransaction(newTransaction)
                .build();
        // Assert
        assertEquals(expectedToString, domeParticipant.toString());
    }

    @Test
    void testDomeParticipantBuilderToString() {
        // Arrange
        String expectedToString = "DomeParticipant.DomeParticipantBuilder(id=" + id
                + ", ethereumAddress=" + ethereumAddress
                + ", newTransaction=" + newTransaction + ")";
        // Act
        DomeParticipant.DomeParticipantBuilder domeParticipantBuilder = DomeParticipant.builder()
                .id(id)
                .ethereumAddress(ethereumAddress)
                .newTransaction(newTransaction);
        // Assert
        assertEquals(expectedToString, domeParticipantBuilder.toString());
    }

}
