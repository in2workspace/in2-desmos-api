package es.in2.desmos.domain.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import static es.in2.desmos.domain.utils.ApplicationUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ApplicationUtilsTests {

    @Test
    void testGetEnvironmentMetadataWithValidProfiles() {
        // Act
        String localMetadata = ApplicationUtils.getEnvironmentMetadata("default");
        String sbxMetadata = ApplicationUtils.getEnvironmentMetadata("dev");
        String devMetadata = ApplicationUtils.getEnvironmentMetadata("test");
        String prdMetadata = ApplicationUtils.getEnvironmentMetadata("prod");
        // Assert
        assertEquals("local", localMetadata);
        assertEquals("sbx", sbxMetadata);
        assertEquals("dev", devMetadata);
        assertEquals("prd", prdMetadata);
    }

    @Test
    void testGetEnvironmentMetadataWithInvalidProfile() {
        // Arrange & Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            ApplicationUtils.getEnvironmentMetadata("invalid");
        });
    }

    @Test
    public void testCalculateSHA256_ValidData() throws NoSuchAlgorithmException {
        // Arrange
        String data = "test";
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] expectedHash = messageDigest.digest(data.getBytes(StandardCharsets.UTF_8));
        String expectedHashString = HexFormat.of().formatHex(expectedHash);
        // Act
        String actualHashString = calculateSHA256(data);
        // Assert
        assertEquals(expectedHashString, actualHashString);
    }

    @Test
    public void testCalculateHashLink_ValidHashes() throws NoSuchAlgorithmException {
        // Arrange
        String previousHash = "5d41402abc4b2a76b9719d911017c592";
        String entityHash = "098f6bcd4621d373cade4e832627b4f6";
        String concatenatedHash = previousHash + entityHash;
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] expectedHash = messageDigest.digest(concatenatedHash.getBytes(StandardCharsets.UTF_8));
        String expectedHashLink = HexFormat.of().formatHex(expectedHash);
        // Act
        String actualHashLink = calculateHashLink(previousHash, entityHash);
        // Assert
        assertEquals(expectedHashLink, actualHashLink);
    }


    @Test
    public void testExtractHashLinkFromDataLocation_ValidDataLocation() {
        // Arrange
        String dataLocation = "http://localhost:8080/ngsi-ld/v1/entities/" +
                "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69" +
                "?hl=03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
        String expectedHashLink = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
        // Act
        String actualHashLink = extractHashLinkFromDataLocation(dataLocation);
        // Assert
        assertEquals(expectedHashLink, actualHashLink);
    }

    @Test
    public void testExtractEntityIdFromDataLocation_ValidDataLocation() {
        // Arrange
        String dataLocation = "http://localhost:8080/ngsi-ld/v1/entities/" +
                "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69" +
                "?hl=03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
        String expectedEntityId = "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69";
        // Act
        String actualEntityId = extractEntityIdFromDataLocation(dataLocation);
        // Assert
        assertEquals(expectedEntityId, actualEntityId);
    }

    @Test
    public void testExtractEntityIdFromDataLocation_InvalidDataLocation() {
        // Arrange
        String dataLocation = "invalid-data-location";
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            extractEntityIdFromDataLocation(dataLocation);
        });
    }

    @Test
    public void testExtractContextBrokerUrlFromDataLocation_ValidDataLocation() {
        // Arrange
        String dataLocation = "http://localhost:8080/ngsi-ld/v1/entities/" +
                "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69" +
                "?hl=03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
        String expectedUrl = "http://localhost:8080/ngsi-ld/v1/entities/" +
                "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69";
        // Act
        String actualUrl = extractContextBrokerUrlFromDataLocation(dataLocation);
        // Assert
        assertEquals(expectedUrl, actualUrl);
    }

}