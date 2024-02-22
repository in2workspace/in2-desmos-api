package es.in2.desmos.api.config;

import es.in2.desmos.api.config.properties.ClientProperties;
import es.in2.desmos.api.exception.HashCreationException;
import es.in2.desmos.api.util.ApplicationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigTest {

    @Mock
    private ClientProperties clientProperties;
    @InjectMocks
    private ApplicationConfig applicationConfig;

    @Test
    void testOrganizationIdHash() {
        // Arrange
        String mockOrganizationId = "org123";
        String expectedHash = "d086b84163a6de3f31d4686dbeca31ab484c6f832e2c70ab5f2171ccef0cfecf";
        // Mock the behavior of organizationId in ClientProperties
        when(clientProperties.organizationId()).thenReturn(mockOrganizationId);
        // Act & Assert
        assertEquals(expectedHash, applicationConfig.organizationIdHash());
    }

    @Test
    void testOrganizationIdHashException() {
        // Arrange
        when(clientProperties.organizationId()).thenThrow(new HashCreationException("simulated error"));
        // Act & Assert
        assertThrows(HashCreationException.class, () -> applicationConfig.organizationIdHash());
    }

    @Test
    void testErrorCreatingHash() {
        // Arrange
        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
            applicationUtils
                    .when(() -> ApplicationUtils.calculateSHA256Hash(any()))
                    .thenThrow(new NoSuchAlgorithmException());
            // Act & Assert
            assertThrows(HashCreationException.class, () -> applicationConfig.organizationIdHash());
        }
    }

}