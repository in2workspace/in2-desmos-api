package es.in2.desmos.application.workflows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.application.workflows.impl.SubscribeWorkflowImpl;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.models.EventQueue;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.api.QueueService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.domain.services.sync.services.DataSyncService;
import es.in2.desmos.objectmothers.BlockchainNotificationMother;
import es.in2.desmos.objectmothers.BrokerDataMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscribeWorkflowTests {

    @Mock
    private QueueService pendingSubscribeEventsQueue;

    @Mock
    private BrokerPublisherService brokerPublisherService;

    @Mock
    private AuditRecordService auditRecordService;

    @Mock
    private DataSyncService dataSyncService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private SubscribeWorkflowImpl subscribeWorkflow;

    @ParameterizedTest
    @ValueSource(strings = {
            BrokerDataMother.GET_REAL_PRODUCT_OFFERING,
            BrokerDataMother.GET_REAL_CATEGORY,
            BrokerDataMother.GET_REAL_CATALOG
    })
    void itShouldVerifyAuditAndPublishedWhenIsRootObject(String retrievedBrokerEntity) throws NoSuchAlgorithmException, JsonProcessingException {
        String processId = "processId";
        String entityId = getEntityId(retrievedBrokerEntity);
        BlockchainNotification blockchainNotification = BlockchainNotificationMother.FromBrokerDataMother(retrievedBrokerEntity);
        EventQueue eventQueueMock = mock(EventQueue.class);

        when(eventQueueMock.getEvent()).thenReturn(List.of(blockchainNotification));
        when(pendingSubscribeEventsQueue.getEventStream()).thenReturn(Flux.just(eventQueueMock));
        when(pendingSubscribeEventsQueue.getEventStream())
                .thenReturn(Flux.just(eventQueueMock));
        when(eventQueueMock.getEvent().get(0))
                .thenReturn(List.of(blockchainNotification));
        when(dataSyncService.getEntityFromExternalSource(processId, blockchainNotification))
                .thenReturn(Flux.just(retrievedBrokerEntity));
        when(dataSyncService.verifyRetrievedEntityData(processId, blockchainNotification, retrievedBrokerEntity))
                .thenReturn(Mono.empty());
        when(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, AuditRecordStatus.RETRIEVED))
                .thenReturn(Mono.empty());
        when(brokerPublisherService.publishDataToBroker(processId, entityId, retrievedBrokerEntity))
                .thenReturn(Mono.empty());
        when(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, AuditRecordStatus.PUBLISHED))
                .thenReturn(Mono.empty());

        StepVerifier.create(subscribeWorkflow.startSubscribeWorkflow(processId))
                .expectNextCount(0)
                .verifyComplete();

        verify(dataSyncService, times(1))
                .getEntityFromExternalSource(processId, blockchainNotification);
        verify(dataSyncService, times(1))
                .verifyRetrievedEntityData(processId, blockchainNotification, retrievedBrokerEntity);
        verify(brokerPublisherService, times(1))
                .publishDataToBroker(processId, entityId, retrievedBrokerEntity);
        verify(auditRecordService, times(1))
                .buildAndSaveAuditRecordFromBlockchainNotification(
                        processId,
                        blockchainNotification,
                        retrievedBrokerEntity,
                        AuditRecordStatus.RETRIEVED);
        verify(auditRecordService, times(1))
                .buildAndSaveAuditRecordFromBlockchainNotification(
                        processId,
                        blockchainNotification,
                        retrievedBrokerEntity,
                        AuditRecordStatus.PUBLISHED);
    }

    @Test
    void itShouldAuditAndPublishedWhenHasRootTypeButIsSubEntity() throws JsonProcessingException {
        String processId = "processId";
        String retrievedBrokerEntity = BrokerDataMother.GET_REAL_PRODUCT_OFFERING;
        String entityId = getEntityId(retrievedBrokerEntity);
        String entityType = getEntityType(retrievedBrokerEntity);
        BlockchainNotification blockchainNotification = BlockchainNotificationMother.Empty();
        EventQueue eventQueueMock = mock(EventQueue.class);

        when(eventQueueMock.getEvent()).thenReturn(List.of(blockchainNotification));
        when(pendingSubscribeEventsQueue.getEventStream()).thenReturn(Flux.just(eventQueueMock));
        when(pendingSubscribeEventsQueue.getEventStream())
                .thenReturn(Flux.just(eventQueueMock));
        when(eventQueueMock.getEvent().get(0))
                .thenReturn(List.of(blockchainNotification));
        when(dataSyncService.getEntityFromExternalSource(processId, blockchainNotification))
                .thenReturn(Flux.just(retrievedBrokerEntity));
        when(auditRecordService.buildAndSaveAuditRecordForSubEntity(processId, entityId, entityType, retrievedBrokerEntity, AuditRecordStatus.RETRIEVED))
                .thenReturn(Mono.empty());
        when(brokerPublisherService.publishDataToBroker(processId, entityId, retrievedBrokerEntity))
                .thenReturn(Mono.empty());
        when(auditRecordService.buildAndSaveAuditRecordForSubEntity(processId, entityId, entityType, retrievedBrokerEntity, AuditRecordStatus.PUBLISHED))
                .thenReturn(Mono.empty());

        StepVerifier.create(subscribeWorkflow.startSubscribeWorkflow(processId))
                .expectNextCount(0)
                .verifyComplete();

        verify(dataSyncService, times(1))
                .getEntityFromExternalSource(processId, blockchainNotification);
        verify(dataSyncService, times(0))
                .verifyRetrievedEntityData(any(), any(), any());
        verify(brokerPublisherService, times(1))
                .publishDataToBroker(processId, entityId, retrievedBrokerEntity);
        verify(auditRecordService, times(1))
                .buildAndSaveAuditRecordForSubEntity(
                        processId,
                        entityId,
                        entityType,
                        retrievedBrokerEntity,
                        AuditRecordStatus.RETRIEVED);
        verify(auditRecordService, times(1))
                .buildAndSaveAuditRecordForSubEntity(
                        processId,
                        entityId,
                        entityType,
                        retrievedBrokerEntity,
                        AuditRecordStatus.PUBLISHED);
    }

    @Test
    void itShouldPublishWhenIsSubEntity() throws JsonProcessingException {
        String processId = "processId";
        String retrievedBrokerEntity = BrokerDataMother.NOT_ROOT_OBJECT;
        String entityId = getEntityId(retrievedBrokerEntity);
        BlockchainNotification blockchainNotification = BlockchainNotificationMother.Empty();
        EventQueue eventQueueMock = mock(EventQueue.class);

        when(eventQueueMock.getEvent()).thenReturn(List.of(blockchainNotification));
        when(pendingSubscribeEventsQueue.getEventStream()).thenReturn(Flux.just(eventQueueMock));
        when(pendingSubscribeEventsQueue.getEventStream())
                .thenReturn(Flux.just(eventQueueMock));
        when(eventQueueMock.getEvent().get(0))
                .thenReturn(List.of(blockchainNotification));
        when(dataSyncService.getEntityFromExternalSource(processId, blockchainNotification))
                .thenReturn(Flux.just(retrievedBrokerEntity));
        when(brokerPublisherService.publishDataToBroker(processId, entityId, retrievedBrokerEntity))
                .thenReturn(Mono.empty());

        StepVerifier.create(subscribeWorkflow.startSubscribeWorkflow(processId))
                .expectNextCount(0)
                .verifyComplete();

        verify(dataSyncService, times(1))
                .getEntityFromExternalSource(processId, blockchainNotification);
        verify(dataSyncService, times(0))
                .verifyRetrievedEntityData(any(), any(), any());
        verify(brokerPublisherService, times(1))
                .publishDataToBroker(processId, entityId, retrievedBrokerEntity);
        verifyNoInteractions(auditRecordService);
    }

    private String getEntityId(String brokerData) throws JsonProcessingException {
        JsonNode jsonEntity = objectMapper.readTree(brokerData);
        return jsonEntity.get("id").asText();
    }

    private String getEntityType(String brokerData) throws JsonProcessingException {
        JsonNode jsonEntity = objectMapper.readTree(brokerData);
        return jsonEntity.get("type").asText();
    }

}