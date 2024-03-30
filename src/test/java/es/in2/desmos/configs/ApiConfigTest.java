package es.in2.desmos.configs;

import es.in2.desmos.configs.properties.OpenApiProperties;
import es.in2.desmos.configs.properties.OrganizationProperties;
import es.in2.desmos.domain.exceptions.HashCreationException;
import es.in2.desmos.domain.utils.ApplicationUtils;
import io.swagger.v3.oas.models.OpenAPI;
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
    private OpenApiProperties openApiProperties;

    @Mock
    private OrganizationProperties organizationProperties;

    @InjectMocks
    private ApiConfig apiConfig;

    @Test
    void givenValidOrganizationId_whenCalculatingHash_thenReturnCorrectHash() {
        // Arrange
        String mockedOrganizationId = "VATES-S9999999E";
        String expectedHastResult = "40b0ab9dfd960064fb7e9fdf77f889c71569e349055ff563e8d699d8fa97fa90";
        when(organizationProperties.organizationId()).thenReturn(mockedOrganizationId);
        // Act & Assert
        assertEquals(expectedHastResult, apiConfig.organizationIdHash());
    }

    @Test
    void givenHashCreationException_whenCalculatingHash_thenThrowHashCreationException() {
        // Arrange
        when(organizationProperties.organizationId()).thenThrow(new HashCreationException("Simulated error"));
        // Act & Assert
        assertThrows(HashCreationException.class, () -> apiConfig.organizationIdHash());
    }

    @Test
    void givenNoSuchAlgorithmException_whenCalculatingHash_thenThrowHashCreationException() {
        // Arrange
        try (MockedStatic<ApplicationUtils> utils = Mockito.mockStatic(ApplicationUtils.class)) {
            utils.when(() -> ApplicationUtils.calculateSHA256(any())).thenThrow(new NoSuchAlgorithmException());
            // Act & Assert
            assertThrows(HashCreationException.class, () -> apiConfig.organizationIdHash());
        }
    }

    @Test
    void openApiBeanIsCorrectlyConfigured() {
        // Arrange
        when(openApiProperties.info()).thenReturn(new OpenApiProperties.InfoProperties(
                "Test Application", "v1.0.0-SNAPSHOT", "Application Description", "https://example.com/terms-of-service",
                new OpenApiProperties.InfoProperties.ContactProperties("operator@example.com", "Operator Name", "https://example.com"),
                new OpenApiProperties.InfoProperties.LicenseProperties("License Name", "https://licenseurl.com")
        ));
        when(openApiProperties.server()).thenReturn(new OpenApiProperties.ServerProperties("https://localhost:8080", "Server Description"));
        // Act
        OpenAPI openApi = apiConfig.openApi();
        // Assert
        assertEquals(1, openApi.getServers().size());
        assertEquals("https://localhost:8080", openApi.getServers().get(0).getUrl());
        assertEquals("Server Description", openApi.getServers().get(0).getDescription());
        assertEquals("License Name", openApi.getInfo().getLicense().getName());
        assertEquals("https://licenseurl.com", openApi.getInfo().getLicense().getUrl());
        assertEquals("operator@example.com", openApi.getInfo().getContact().getEmail());
        assertEquals("Operator Name", openApi.getInfo().getContact().getName());
        assertEquals("https://example.com", openApi.getInfo().getContact().getUrl());
    }

}
