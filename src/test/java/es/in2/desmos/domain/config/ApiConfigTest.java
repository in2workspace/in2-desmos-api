package es.in2.desmos.domain.config;

import es.in2.desmos.infrastructure.configs.ApiConfig;
import es.in2.desmos.infrastructure.configs.properties.ClientProperties;
import es.in2.desmos.domain.exception.HashCreationException;
import es.in2.desmos.domain.util.ApplicationUtils;
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
class ApiConfigTest {

    @Mock
    private ClientProperties clientProperties;
    @InjectMocks
    private ApiConfig apiConfig;

    @Test
    void testOrganizationIdHash() {
        // Arrange
        String mockOrganizationId = "org123";
        String expectedHash = "d086b84163a6de3f31d4686dbeca31ab484c6f832e2c70ab5f2171ccef0cfecf";
        // Mock the behavior of organizationId in ClientProperties
        when(clientProperties.organizationId()).thenReturn(mockOrganizationId);
        // Act & Assert
        assertEquals(expectedHash, apiConfig.organizationIdHash());
    }

    @Test
    void testOrganizationIdHashException() {
        // Arrange
        when(clientProperties.organizationId()).thenThrow(new HashCreationException("simulated error"));
        // Act & Assert
        assertThrows(HashCreationException.class, () -> apiConfig.organizationIdHash());
    }

    @Test
    void testErrorCreatingHash() {
        // Arrange
        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
            applicationUtils
                    .when(() -> ApplicationUtils.calculateSHA256(any()))
                    .thenThrow(new NoSuchAlgorithmException());
            // Act & Assert
            assertThrows(HashCreationException.class, () -> apiConfig.organizationIdHash());
        }
    }

}