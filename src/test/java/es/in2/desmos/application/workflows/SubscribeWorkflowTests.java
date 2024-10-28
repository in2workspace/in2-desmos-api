package es.in2.desmos.application.workflows;

import es.in2.desmos.application.workflows.impl.SubscribeWorkflowImpl;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.models.EventQueue;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.api.QueueService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.domain.services.sync.services.DataSyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscribeWorkflowTests {

    private final long id = 1234;
    private final String publisherAddress = "http://blockchain-testnode.infra.svc.cluster.local:8545/";
    private final String eventType = "ProductOffering";
    private final long timestamp = 1711801566;
    private final String dataLocation = "http://localhost:8080/ngsi-ld/v1/entities/" +
            "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69" +
            "?hl=03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
    private final List<String> relevantMetadata = List.of("metadata1", "metadata2");
    private final String entityIdHash = "6f6468ded8276d009ab1b6c578c2b922053acd6b5a507f36d408d3f7c9ae91d0";
    private final String previousEntityHashLink = "98d9658d98764dbe135b316f52a98116b4b02f9d7e57212aa86335c42a58539a";

    BlockchainNotification blockchainNotification = BlockchainNotification.builder()
            .id(id)
            .publisherAddress(publisherAddress)
            .eventType(eventType)
            .timestamp(timestamp)
            .dataLocation(dataLocation)
            .relevantMetadata(relevantMetadata)
            .entityId(entityIdHash)
            .previousEntityHashLink(previousEntityHashLink)
            .build();

    @Mock
    private QueueService pendingSubscribeEventsQueue;
    @Mock
    private BrokerPublisherService brokerPublisherService;
    @Mock
    private AuditRecordService auditRecordService;
    @Mock
    private DataSyncService dataSyncService;
    @InjectMocks
    private SubscribeWorkflowImpl subscribeWorkflow;

    @Test
    void testStartSubscribeWorkflow() {
        // Arrange
        String processId = "processId";
        String retrievedBrokerEntity = "retrievedBrokerEntity";
        EventQueue eventQueueMock = mock(EventQueue.class);

        when(eventQueueMock.getEvent()).thenReturn(List.of(blockchainNotification));
        when(pendingSubscribeEventsQueue.getEventStream()).thenReturn(Flux.just(eventQueueMock));
        when(pendingSubscribeEventsQueue.getEventStream())
                .thenReturn(Flux.just(eventQueueMock));
        when(eventQueueMock.getEvent().get(0))
                .thenReturn(List.of(blockchainNotification));
        when(dataSyncService.getEntityFromExternalSource(processId, blockchainNotification))
                .thenReturn(Mono.just(retrievedBrokerEntity));
        when(dataSyncService.verifyRetrievedEntityData(processId, blockchainNotification, retrievedBrokerEntity))
                .thenReturn(Mono.empty());
        when(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, AuditRecordStatus.RETRIEVED))
                .thenReturn(Mono.empty());
        when(brokerPublisherService.publishDataToBroker(processId, blockchainNotification, retrievedBrokerEntity))
                .thenReturn(Mono.empty());
        when(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, AuditRecordStatus.PUBLISHED))
                .thenReturn(Mono.empty());

        // Act
        subscribeWorkflow.startSubscribeWorkflow(processId).blockLast();

        // Assert
        verify(dataSyncService).getEntityFromExternalSource(processId, blockchainNotification);
        verify(dataSyncService).verifyRetrievedEntityData(processId, blockchainNotification, retrievedBrokerEntity);
        verify(brokerPublisherService).publishDataToBroker(processId, blockchainNotification, retrievedBrokerEntity);
        verify(auditRecordService, times(1)).buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, AuditRecordStatus.RETRIEVED);
    }

}