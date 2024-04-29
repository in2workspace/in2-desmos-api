package es.in2.desmos.domain.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ApplicationUtilsTest {
    @Test
    void testGetEnvironmentMetadataWithValidProfiles() {
        // Arrange & Act
        String localMetadata = ApplicationUtils.getEnvironmentMetadata("default");
        String sbxMetadata = ApplicationUtils.getEnvironmentMetadata("dev");
        String devMetadata = ApplicationUtils.getEnvironmentMetadata("test");
        String prdMetadata = ApplicationUtils.getEnvironmentMetadata("prod");

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
}