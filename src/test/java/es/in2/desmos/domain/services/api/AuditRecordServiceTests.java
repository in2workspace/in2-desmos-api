package es.in2.desmos.domain.services.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.AuditRecordTrader;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.repositories.AuditRecordRepository;
import es.in2.desmos.domain.services.api.impl.AuditRecordServiceImpl;
import es.in2.desmos.objectmothers.AuditRecordMother;
import es.in2.desmos.objectmothers.EntitySyncResponseMother;
import es.in2.desmos.objectmothers.MVEntity4DataNegotiationMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditRecordServiceTests {

    @InjectMocks
    private AuditRecordServiceImpl auditRecordService;

    @Mock
    private AuditRecordRepository auditRecordRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Captor
    private ArgumentCaptor<AuditRecord> auditRecordArgumentCaptor;

    @Test
    void itShouldBuildAndSaveAuditRecordFromDataSync() {
        String processId = "0";
        String issuer = "http://example.org";
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample1();
        String retrievedBrokerEntity = EntitySyncResponseMother.sample();
        AuditRecordStatus status = AuditRecordStatus.RETRIEVED;

        AuditRecord expectedAuditRecord = AuditRecordMother.createAuditRecordFromMVEntity4DataNegotiation("http://example.org", mvEntity4DataNegotiation, status);

        when(auditRecordRepository.findMostRecentAuditRecord()).thenReturn(Mono.just(new AuditRecord()));

        when(auditRecordRepository.save(any())).thenReturn(Mono.just(expectedAuditRecord));

        var result = auditRecordService.buildAndSaveAuditRecordFromDataSync(processId, issuer, mvEntity4DataNegotiation, retrievedBrokerEntity, status);

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
    void itShouldBuildAndSaveAuditRecordFromDataSyncWhenEntityIsNull() {
        String processId = "0";
        String issuer = "http://example.org";
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample1();
        String retrievedBrokerEntity = null;
        AuditRecordStatus status = AuditRecordStatus.RETRIEVED;

        AuditRecord expectedAuditRecord =
                AuditRecord.builder()
                        .id(UUID.randomUUID())
                        .processId(processId)
                        .createdAt(Timestamp.from(Instant.now()))
                        .entityId(mvEntity4DataNegotiation.id())
                        .entityType(mvEntity4DataNegotiation.type())
                        .entityHash("")
                        .entityHashLink("")
                        .dataLocation("")
                        .status(status)
                        .trader(AuditRecordTrader.CONSUMER)
                        .hash("")
                        .hashLink("")
                        .newTransaction(true)
                        .build();

        when(auditRecordRepository.findMostRecentAuditRecord()).thenReturn(Mono.just(new AuditRecord()));

        when(auditRecordRepository.save(any())).thenReturn(Mono.just(expectedAuditRecord));

        //noinspection ConstantValue
        var result = auditRecordService.buildAndSaveAuditRecordFromDataSync(processId, issuer, mvEntity4DataNegotiation, retrievedBrokerEntity, status);

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
    void itShouldBuildAndSaveAuditRecordFromDataSyncWhenEntityIsBlank() {
        String processId = "0";
        String issuer = "http://example.org";
        MVEntity4DataNegotiation mvEntity4DataNegotiation = MVEntity4DataNegotiationMother.sample1();
        String retrievedBrokerEntity = "";
        AuditRecordStatus status = AuditRecordStatus.RETRIEVED;

        AuditRecord expectedAuditRecord =
                AuditRecord.builder()
                        .id(UUID.randomUUID())
                        .processId(processId)
                        .createdAt(Timestamp.from(Instant.now()))
                        .entityId(mvEntity4DataNegotiation.id())
                        .entityType(mvEntity4DataNegotiation.type())
                        .entityHash("")
                        .entityHashLink("")
                        .dataLocation("")
                        .status(status)
                        .trader(AuditRecordTrader.CONSUMER)
                        .hash("")
                        .hashLink("")
                        .newTransaction(true)
                        .build();

        when(auditRecordRepository.findMostRecentAuditRecord()).thenReturn(Mono.just(new AuditRecord()));

        when(auditRecordRepository.save(any())).thenReturn(Mono.just(expectedAuditRecord));

        var result = auditRecordService.buildAndSaveAuditRecordFromDataSync(processId, issuer, mvEntity4DataNegotiation, retrievedBrokerEntity, status);

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
}