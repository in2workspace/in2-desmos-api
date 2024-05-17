package es.in2.desmos.application.workflows;

import es.in2.desmos.application.workflows.impl.PublishWorkflowImpl;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.BlockchainTxPayload;
import es.in2.desmos.domain.models.BrokerNotification;
import es.in2.desmos.domain.models.EventQueue;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.api.QueueService;
import es.in2.desmos.domain.services.blockchain.BlockchainPublisherService;
import es.in2.desmos.domain.utils.BlockchainTxPayloadFactory;
import es.in2.desmos.objectmothers.BrokerNotificationMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublishWorkflowTests {

    private final String processId = "processId";
    private final String id = "1234";
    private final String type = "TestType";
    private final List<Map<String, Object>> data = List.of(Collections.singletonMap("id", "123"));
    private final String subscriptionId = "urn:ngsi-ld:Subscription:7230a0ac-ed33-44e4-bb04-ce0f345b9d2a";
    private final String notifiedAt = "2024-01-01T12:00:00Z";

    BrokerNotification brokerNotification = BrokerNotification.builder()
            .id(id)
            .type(type)
            .data(data)
            .subscriptionId(subscriptionId)
            .notifiedAt(notifiedAt)
            .build();

    @Mock
    private QueueService pendingPublishEventsQueue;
    @Mock
    private AuditRecordService auditRecordService;
    @Mock
    private BlockchainTxPayloadFactory blockchainTxPayloadFactory;
    @Mock
    private BlockchainPublisherService blockchainPublisherService;
    @InjectMocks
    private PublishWorkflowImpl publishWorkflow;


    @Test
    void testBuildBlockchainTxPayload() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", "123");
        String previousHash = "5d41402abc4b2a76b9719d911017c592";
        EventQueue eventQueueMock = mock(EventQueue.class);

        BlockchainTxPayload blockchainTxPayload = BlockchainTxPayload.builder().build();

        when(eventQueueMock.getEvent()).thenReturn(List.of(brokerNotification));
        when(pendingPublishEventsQueue.getEventStream()).thenReturn(Flux.just(eventQueueMock));
        when(eventQueueMock.getEvent().get(0)).thenReturn(List.of(brokerNotification));
        when(auditRecordService.fetchLatestProducerEntityHashByEntityId(processId, "123")).thenReturn(Mono.empty());
        when(blockchainTxPayloadFactory.calculatePreviousHashIfEmpty(eq(processId), any())).thenReturn(Mono.just(previousHash));
        when(blockchainTxPayloadFactory.buildBlockchainTxPayload(anyString(), anyMap(), anyString()))
                .thenReturn(Mono.just(blockchainTxPayload));
        when(auditRecordService.buildAndSaveAuditRecordFromBrokerNotification(eq(processId), any(), eq(AuditRecordStatus.CREATED), any()))
                .thenReturn(Mono.empty());
        when(blockchainPublisherService.publishDataToBlockchain(processId, blockchainTxPayload))
                .thenReturn(Mono.empty());
        when(auditRecordService.buildAndSaveAuditRecordFromBrokerNotification(eq(processId), any(), eq(AuditRecordStatus.PUBLISHED), any()))
                .thenReturn(Mono.empty());

        publishWorkflow.startPublishWorkflow(processId).blockLast();

        verify(blockchainTxPayloadFactory).buildBlockchainTxPayload(processId, data, previousHash);
        verify(auditRecordService).buildAndSaveAuditRecordFromBrokerNotification(eq(processId), any(), eq(AuditRecordStatus.CREATED), any());
        verify(blockchainPublisherService).publishDataToBlockchain(eq(processId), any());
        verify(auditRecordService).buildAndSaveAuditRecordFromBrokerNotification(eq(processId), any(), eq(AuditRecordStatus.PUBLISHED), any());
    }

    @Test
    void itShouldProcessBrokerNotificationWhenIsAnArrayOfEntities() {
        EventQueue eventQueueMock = mock(EventQueue.class);
        when(pendingPublishEventsQueue.getEventStream()).thenReturn(Flux.just(eventQueueMock));
        BrokerNotification currentBrokerNotification = BrokerNotificationMother.withDataArray();
        when(eventQueueMock.getEvent()).thenReturn(List.of(currentBrokerNotification));

        String id0 = currentBrokerNotification.data().get(0).get("id").toString();
        when(auditRecordService.fetchLatestProducerEntityHashByEntityId(processId, id0)).thenReturn(Mono.empty());
        String id1 = currentBrokerNotification.data().get(1).get("id").toString();
        when(auditRecordService.fetchLatestProducerEntityHashByEntityId(processId, id1)).thenReturn(Mono.empty());

        String previousHash = "5d41402abc4b2a76b9719d911017c592";
        when(blockchainTxPayloadFactory.calculatePreviousHashIfEmpty(eq(processId), any())).thenReturn(Mono.just(previousHash));

        BlockchainTxPayload blockchainTxPayload = BlockchainTxPayload.builder().build();
        when(blockchainTxPayloadFactory.buildBlockchainTxPayload(anyString(), anyMap(), anyString()))
                .thenReturn(Mono.just(blockchainTxPayload));
        when(auditRecordService.buildAndSaveAuditRecordFromBrokerNotification(eq(processId), any(), eq(AuditRecordStatus.CREATED), any()))
                .thenReturn(Mono.empty());
        when(blockchainPublisherService.publishDataToBlockchain(processId, blockchainTxPayload))
                .thenReturn(Mono.empty());
        when(auditRecordService.buildAndSaveAuditRecordFromBrokerNotification(eq(processId), any(), eq(AuditRecordStatus.PUBLISHED), any()))
                .thenReturn(Mono.empty());

        publishWorkflow.startPublishWorkflow(processId).blockLast();

        verify(pendingPublishEventsQueue).getEventStream();
        verifyNoMoreInteractions(pendingPublishEventsQueue);

        verify(eventQueueMock).getEvent();
        verifyNoMoreInteractions(eventQueueMock);

        var dataMap0 = currentBrokerNotification.data().get(0);
        verify(blockchainTxPayloadFactory).calculatePreviousHashIfEmpty(processId, dataMap0);
        verify(blockchainTxPayloadFactory).buildBlockchainTxPayload(processId, dataMap0, previousHash);
        var dataMap1 = currentBrokerNotification.data().get(1);
        verify(blockchainTxPayloadFactory).calculatePreviousHashIfEmpty(processId, dataMap1);
        verify(blockchainTxPayloadFactory).buildBlockchainTxPayload(processId, dataMap1, previousHash);
        verifyNoMoreInteractions(blockchainTxPayloadFactory);

        verify(blockchainPublisherService, times(2)).publishDataToBlockchain(eq(processId), any());
        verifyNoMoreInteractions(blockchainPublisherService);

        verify(auditRecordService).buildAndSaveAuditRecordFromBrokerNotification(eq(processId), eq(dataMap0), eq(AuditRecordStatus.CREATED), any());
        verify(auditRecordService).buildAndSaveAuditRecordFromBrokerNotification(eq(processId), eq(dataMap1), eq(AuditRecordStatus.CREATED), any());
        verify(auditRecordService).buildAndSaveAuditRecordFromBrokerNotification(eq(processId), eq(dataMap0), eq(AuditRecordStatus.PUBLISHED), any());
        verify(auditRecordService).buildAndSaveAuditRecordFromBrokerNotification(eq(processId), eq(dataMap1), eq(AuditRecordStatus.PUBLISHED), any());
        verifyNoMoreInteractions(auditRecordService);
    }
}