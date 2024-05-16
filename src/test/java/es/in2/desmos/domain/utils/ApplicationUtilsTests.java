package es.in2.desmos.domain.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.utils.testdata.ObjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.stream.Stream;

import static es.in2.desmos.domain.utils.ApplicationUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApplicationUtilsTests {

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                Arguments.of(ObjectMother.getObjectInput(), ObjectMother.getObjectInputExpected()),
                Arguments.of(ObjectMother.getArrayInput(), ObjectMother.getArrayInputExpected()),
                Arguments.of(ObjectMother.getPrimitiveInput(), ObjectMother.getPrimitiveInputExpected()),
                Arguments.of(ObjectMother.getArrayInput2(), ObjectMother.getArrayInput2Expected())
        );
    }

    @Test
    void testConstructorThrowsException() throws NoSuchMethodException {
        Constructor<ApplicationUtils> constructor = ApplicationUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            fail("Expected an exception to be thrown");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            assertInstanceOf(IllegalStateException.class, e.getCause());
        }
    }

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
    void testCalculateSHA256_ValidData() throws NoSuchAlgorithmException {
        // Arrange
        String data = """
                {
                   "id": "notification:-5106976853901020699",
                   "type": "Notification",
                   "data": [
                     {
                       "id": "urn:ngsi-ld:ProductOffering:122355255",
                       "type": "ProductOffering",
                       "description": {
                         "type": "Property",
                         "value": "Example of a Product offering for cloud services suite"
                       },
                       "notifiedAt": "2024-04-10T11:33:43.807Z"
                     }
                   ],
                   "subscriptionId": "urn:ngsi-ld:Subscription:122355255",
                   "notifiedAt": "2023-03-14T16:38:15.123456Z"
                 }
                """;
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] expectedHash = messageDigest.digest(data.getBytes(StandardCharsets.UTF_8));
        // Act & Assert
        assertDoesNotThrow(() -> calculateSHA256(data));
    }

    @Test
    void testCalculateHashLink_ValidHashes() throws NoSuchAlgorithmException, JsonProcessingException {
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
    void testExtractHashLinkFromDataLocation_ValidDataLocation() {
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
    void testExtractEntityIdFromDataLocation_ValidDataLocation() {
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
    void testExtractEntityIdFromDataLocation_InvalidDataLocation() {
        // Arrange
        String dataLocation = "invalid-data-location";
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            extractEntityIdFromDataLocation(dataLocation);
        });
    }

    @Test
    void testExtractContextBrokerUrlFromDataLocation_ValidDataLocation() {
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

    @ParameterizedTest
    @MethodSource("provideTestData")
    void testVerifySortAttributesAlphabetically(String input, String expected) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, JsonProcessingException {
        //Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        Method method = ApplicationUtils.class.getDeclaredMethod("sortAttributesAlphabetically", String.class, ObjectMapper.class);
        method.setAccessible(true);
        //Act
        String result = (String) method.invoke(null, input, objectMapper);
        //Assert
        String normalizedExpected = expected.replace("\r\n", "\n");
        String normalizedResult = result.replace("\r\n", "\n");
        assertEquals(normalizedExpected, normalizedResult);
    }


}