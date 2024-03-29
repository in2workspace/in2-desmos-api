package es.in2.desmos.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.model.AuditRecord;
import es.in2.desmos.infrastructure.configs.ApiConfig;
import es.in2.desmos.domain.exception.HashLinkException;
import es.in2.desmos.domain.model.DLTEvent;
import es.in2.desmos.domain.service.impl.DLTEventCreatorServiceImpl;
import es.in2.desmos.domain.util.ApplicationUtils;
import es.in2.desmos.infrastructure.configs.BrokerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DLTEventCreatorServiceTests {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private BrokerConfig brokerConfig;
    @Mock
    private ApiConfig apiConfig;
    @Mock
    private AuditRecordService auditRecordService;
    @InjectMocks
    private DLTEventCreatorServiceImpl dltEventCreatorService;

    @Test
    void createDLTEvent_Success() throws JsonProcessingException, NoSuchAlgorithmException {
        // Arrange
        String processId = "d670e989-eaf0-4916-aa8c-59d4de38b27e";
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", "urn:ngsi-ld:ProductOffering:12345678");
        dataMap.put("type", "ProductOffering");
        when(objectMapper.writeValueAsString(any())).thenReturn("sampleData");
        when(apiConfig.organizationIdHash()).thenReturn("ec22a787d8...3365894b3c");
        when(brokerConfig.getEntitiesExternalDomain()).thenReturn("https://example.com/ngsi-ld/v1/entities");
        when(auditRecordService.saveAuditRecord(anyString(), any(AuditRecord.class))).thenReturn(Mono.empty());
        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
            applicationUtils.when(() -> ApplicationUtils.calculateSHA256(anyString()))
                    .thenReturn("calculatedHash");
            // Act
            Mono<DLTEvent> resultMono = dltEventCreatorService.buildDLTEvent(processId, dataMap, "previousEntityHash");
            // Assert
            DLTEvent result = resultMono.block(); // Blocks until the Mono is completed
            assert result != null;
            verify(auditRecordService, times(1)).saveAuditRecord(anyString(), any(AuditRecord.class));
        }
    }

    @Test
    void createDLTEvent_WithHashLinkException() throws JsonProcessingException {
        // Arrange
        String processId = "d670e989-eaf0-4916-aa8c-59d4de38b27e";
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", "sampleId");
        dataMap.put("type", "ProductOffering");
        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
            applicationUtils.when(() -> ApplicationUtils.calculateSHA256(anyString()))
                    .thenThrow(new NoSuchAlgorithmException());
            // Act
            Mono<DLTEvent> resultMono = dltEventCreatorService.buildDLTEvent(processId, dataMap, "previousEntityHash");
            // Assert
            assertThrows(RuntimeException.class, resultMono::block);
        }
    }

}
