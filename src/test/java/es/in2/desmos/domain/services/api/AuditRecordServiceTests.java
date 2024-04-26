package es.in2.desmos.domain.services.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.models.BlockchainTxPayload;
import es.in2.desmos.domain.repositories.AuditRecordRepository;
import es.in2.desmos.domain.services.api.impl.AuditRecordServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditRecordServiceTests {

    @Mock
    private AuditRecordRepository auditRecordRepository;

    @Mock
    private BlockchainTxPayload blockchainTxPayload;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuditRecordServiceImpl auditRecordService;

    @Test
    void testBuildAndSaveAuditRecordFromBrokerNotification() throws Exception {
        // Arrange
        String processId = "processId";
        String sampleDataLocation = "http://localhost:8080/ngsi-ld/v1/entities/" +
                "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69" +
                "?hl=03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", "entityId");
        dataMap.put("type", "entityType");
        AuditRecordStatus status = AuditRecordStatus.CREATED;
        AuditRecord lastAuditRecordRegistered = AuditRecord.builder()
                .hashLink("previousHashLink")
                .build();

        when(objectMapper.writeValueAsString(any())).thenReturn("sampleData");
        when(auditRecordRepository.findMostRecentAuditRecord()).thenReturn(Mono.just(lastAuditRecordRegistered));
        when(blockchainTxPayload.dataLocation()).thenReturn(sampleDataLocation);
        when(auditRecordRepository.save(any())).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = auditRecordService.buildAndSaveAuditRecordFromBrokerNotification(processId, dataMap, status, blockchainTxPayload);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(auditRecordRepository, times(1)).save(any());
    }
    
    @Test
    void testBuildAndSaveAuditRecordFromBlockchainNotification() throws Exception {
        // Arrange
        String processId = "processId";
        String sampleDataLocation = "http://localhost:8080/ngsi-ld/v1/entities/" +
                "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69" +
                "?hl=03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
        BlockchainNotification blockchainNotification = BlockchainNotification.builder()
                .dataLocation(sampleDataLocation)
                .eventType("entityType")
                .build();
        String retrievedBrokerEntity = "sampleData";
        AuditRecordStatus status = AuditRecordStatus.CREATED;
        AuditRecord lastAuditRecordRegistered = AuditRecord.builder()
                .hashLink("previousHashLink")
                .build();

        when(objectMapper.writeValueAsString(any())).thenReturn("sampleData");
        when(auditRecordRepository.findMostRecentAuditRecord()).thenReturn(Mono.just(lastAuditRecordRegistered));
        when(auditRecordRepository.save(any())).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, status);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(auditRecordRepository, times(1)).save(any());
    }

}