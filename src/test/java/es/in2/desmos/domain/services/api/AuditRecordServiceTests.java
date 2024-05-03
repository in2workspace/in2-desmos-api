package es.in2.desmos.domain.services.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.models.BlockchainTxPayload;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.repositories.AuditRecordRepository;
import es.in2.desmos.domain.services.api.impl.AuditRecordServiceImpl;
import es.in2.desmos.objectmothers.AuditRecordMother;
import es.in2.desmos.objectmothers.MVEntity4DataNegotiationMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditRecordServiceTests {

    @Mock
    private AuditRecordRepository auditRecordRepository;

    @Mock
    private BlockchainTxPayload blockchainTxPayload;

    @SuppressWarnings("CanBeFinal")
    @Spy
    private static ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AuditRecordServiceImpl auditRecordService;
    @Captor
    private ArgumentCaptor<AuditRecord> auditRecordArgumentCaptor;

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
    void itShouldBuildAndSaveAuditRecordFromDataSync() {
        String processId = "0";
        String issuer = "http://example.org";
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample1();
        AuditRecordStatus status = AuditRecordStatus.RETRIEVED;
        AuditRecord expectedAuditRecord = AuditRecordMother.createAuditRecordFromMVEntity4DataNegotiation("http://example.org", mvEntity4DataNegotiation, status);

        when(auditRecordRepository.findMostRecentAuditRecord()).thenReturn(Mono.just(new AuditRecord()));
        when(auditRecordRepository.save(any())).thenReturn(Mono.just(expectedAuditRecord));

        var result = auditRecordService.buildAndSaveAuditRecordFromDataSync(processId, issuer, mvEntity4DataNegotiation, status);

        StepVerifier
                .create(result)
                .verifyComplete();

        verify(auditRecordRepository, times(1)).save(auditRecordArgumentCaptor.capture());
        verifyNoMoreInteractions(auditRecordRepository);

        assertThat(auditRecordArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "hash", "hashLink")
                .isEqualTo(expectedAuditRecord);
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

    @Test
    void itShouldReturnErrorWhenAuditRecordCreatesIncorrectJson() throws JsonProcessingException {
        String processId = "0";
        String issuer = "http://example.org";
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample1();
        AuditRecordStatus status = AuditRecordStatus.RETRIEVED;

        when(auditRecordRepository.findMostRecentAuditRecord()).thenReturn(Mono.just(new AuditRecord()));

        when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        var result = auditRecordService.buildAndSaveAuditRecordFromDataSync(processId, issuer, mvEntity4DataNegotiation, status);

        StepVerifier
                .create(result)
                .expectErrorMatches(throwable -> throwable instanceof JsonProcessingException)
                .verify();
    }

}