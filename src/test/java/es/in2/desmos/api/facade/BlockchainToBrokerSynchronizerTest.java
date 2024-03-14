package es.in2.desmos.api.facade;

import es.in2.desmos.api.facade.impl.BlockchainToBrokerSynchronizerImpl;
import es.in2.desmos.api.model.BlockchainNotification;
import es.in2.desmos.api.model.EventQueue;
import es.in2.desmos.api.model.EventQueuePriority;
import es.in2.desmos.api.service.BrokerEntityPublisherService;
import es.in2.desmos.api.service.BrokerEntityRetrievalService;
import es.in2.desmos.api.service.NotificationProcessorService;
import es.in2.desmos.api.service.QueueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BlockchainToBrokerSynchronizerTest {

    // Test data
    private final String processId = "testProcessId";
    private final BlockchainNotification blockchainNotification = BlockchainNotification.builder()
            .id(5478474)
            .publisherAddress("publisherAddress").eventType("eventType")
            .timestamp(684485648)
            .dataLocation("dataLocation").relevantMetadata(List.of("metadata1", "metadata2")).build();
    @Mock
    private NotificationProcessorService notificationProcessorService;
    @Mock
    private BrokerEntityRetrievalService brokerEntityRetrievalService;
    @Mock
    private BrokerEntityPublisherService brokerEntityPublisherService;
    @Mock
    private QueueService blockchainToBrokerQueueService;

    @InjectMocks
    private BlockchainToBrokerSynchronizerImpl synchronizer;

    @Test
    void testRetrieveAndPublishEntityIntoBroker() {
        // Mocking the behavior of each service in the workflow
        when(notificationProcessorService.processBlockchainNotification(processId, blockchainNotification))
                .thenReturn(Mono.empty());
        when(brokerEntityRetrievalService.retrieveEntityFromSourceBroker(processId, blockchainNotification))
                .thenReturn(Mono.just("entityString"));
        when(brokerEntityPublisherService.publishRetrievedEntityToBroker(processId, "entityString", blockchainNotification))
                .thenReturn(Mono.empty());
        // Act
        synchronizer.retrieveAndPublishEntityToBroker(processId, blockchainNotification).block();
        // Assert
        verify(notificationProcessorService).processBlockchainNotification(processId, blockchainNotification);
        verify(brokerEntityRetrievalService).retrieveEntityFromSourceBroker(processId, blockchainNotification);
        verify(brokerEntityPublisherService).publishRetrievedEntityToBroker(processId, "entityString", blockchainNotification);
    }

    @Test
    void testStartProcessingEventsSuccessfulFlow() {
        // Arrange
        EventQueue eventQueue = new EventQueue(List.of(blockchainNotification), EventQueuePriority.PUBLICATION_PUBLISH);

        when(blockchainToBrokerQueueService.getEventStream()).thenReturn(Flux.just(eventQueue));

        when(notificationProcessorService.processBlockchainNotification(anyString(), any(BlockchainNotification.class)))
                .thenReturn(Mono.empty());

        when(brokerEntityRetrievalService.retrieveEntityFromSourceBroker(anyString(), any(BlockchainNotification.class)))
                .thenReturn(Mono.just("entityString"));

        when(brokerEntityPublisherService.publishRetrievedEntityToBroker(anyString(), anyString(), any(BlockchainNotification.class)))
                .thenReturn(Mono.empty());

        // Act and Assert
        StepVerifier.create(synchronizer.startProcessingEvents())
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testStartProcessingEventsSuccessfulFlowRecover() {
        // Arrange
        EventQueue eventQueue = new EventQueue(List.of(blockchainNotification, "Entity"), EventQueuePriority.RECOVER_PUBLISH);

        when(blockchainToBrokerQueueService.getEventStream()).thenReturn(Flux.just(eventQueue));

        when(notificationProcessorService.processBlockchainNotification(anyString(), any(BlockchainNotification.class)))
                .thenReturn(Mono.empty());

        when(brokerEntityRetrievalService.retrieveEntityFromSourceBroker(anyString(), any(BlockchainNotification.class)))
                .thenReturn(Mono.just("entityString"));

        when(brokerEntityPublisherService.publishRetrievedEntityToBroker(anyString(), anyString(), any(BlockchainNotification.class)))
                .thenReturn(Mono.empty());

        // Act and Assert
        StepVerifier.create(synchronizer.startProcessingEvents())
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
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
        verify(brokerEntityPublisherService, never()).publishRetrievedEntityToBroker(anyString(), anyString(), any());
    }

}
