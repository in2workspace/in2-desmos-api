package es.in2.desmos.api.facade;

import es.in2.desmos.api.facade.impl.BlockchainToBrokerSynchronizerImpl;
import es.in2.desmos.api.model.BlockchainNotification;
import es.in2.desmos.api.service.BrokerEntityPublicationService;
import es.in2.desmos.api.service.BrokerEntityRetrievalService;
import es.in2.desmos.api.service.NotificationProcessorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockchainToBrokerSynchronizerTest {

    // Test data
    private final String processId = "testProcessId";
    private final BlockchainNotification blockchainNotification = BlockchainNotification.builder()
            .id(BlockchainNotification.Id.builder().type("type").hex("hex").build())
            .publisherAddress("publisherAddress").eventType("eventType")
            .timestamp(BlockchainNotification.Timestamp.builder().type("type").hex("hex").build())
            .dataLocation("dataLocation").relevantMetadata(List.of("metadata1", "metadata2")).build();
    @Mock
    private NotificationProcessorService notificationProcessorService;
    @Mock
    private BrokerEntityRetrievalService brokerEntityRetrievalService;
    @Mock
    private BrokerEntityPublicationService brokerEntityPublicationService;
    @InjectMocks
    private BlockchainToBrokerSynchronizerImpl synchronizer;

    @Test
    void testRetrieveAndPublishEntityIntoBroker() {
        // Mocking the behavior of each service in the workflow
        when(notificationProcessorService.processBlockchainNotification(processId, blockchainNotification))
                .thenReturn(Mono.empty());
        when(brokerEntityRetrievalService.retrieveEntityFromSourceBroker(processId, blockchainNotification))
                .thenReturn(Mono.just("entityString"));
        when(brokerEntityPublicationService.publishRetrievedEntityToBroker(processId, "entityString", blockchainNotification))
                .thenReturn(Mono.empty());
        // Act
        synchronizer.retrieveAndPublishEntityToBroker(processId, blockchainNotification).block();
        // Assert
        verify(notificationProcessorService).processBlockchainNotification(processId, blockchainNotification);
        verify(brokerEntityRetrievalService).retrieveEntityFromSourceBroker(processId, blockchainNotification);
        verify(brokerEntityPublicationService).publishRetrievedEntityToBroker(processId, "entityString", blockchainNotification);
    }

    @Test
    void testRetrieveAndPublishEntityIntoBrokerWithError() {
        // Mocking the behavior to simulate an error during entity retrieval
        when(notificationProcessorService.processBlockchainNotification(processId, blockchainNotification))
                .thenReturn(Mono.empty());
        when(brokerEntityRetrievalService.retrieveEntityFromSourceBroker(processId, blockchainNotification))
                .thenReturn(Mono.error(new RuntimeException("Test Exception")));
        // Act
        synchronizer.retrieveAndPublishEntityToBroker(processId, blockchainNotification)
                .onErrorResume(e -> Mono.empty())
                .block();
        // Assert
        verify(notificationProcessorService).processBlockchainNotification(processId, blockchainNotification);
        verify(brokerEntityRetrievalService).retrieveEntityFromSourceBroker(processId, blockchainNotification);
        // Verificar que la publicación no se realiza debido al error
        verify(brokerEntityPublicationService, never()).publishRetrievedEntityToBroker(anyString(), anyString(), any());
    }

}
