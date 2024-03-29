package es.in2.desmos.application.service;

import es.in2.desmos.application.service.impl.DataRetrievalServiceImpl;
import es.in2.desmos.domain.model.DLTNotification;
import es.in2.desmos.domain.model.EventQueue;
import es.in2.desmos.domain.model.EventQueuePriority;
import es.in2.desmos.domain.service.BrokerEntityPublisherService;
import es.in2.desmos.domain.service.BrokerEntityRetrievalService;
import es.in2.desmos.domain.service.QueueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataRetrievalServiceTest {

    @Mock
    private BrokerEntityRetrievalService brokerEntityRetrievalService;
    @Mock
    private BrokerEntityPublisherService brokerEntityPublisherService;
    @Mock
    private QueueService dataRetrievalQueue;
    @InjectMocks
    private DataRetrievalServiceImpl dataRetrievalService;

    // Test data
    private final DLTNotification dltNotification = DLTNotification.builder()
            .id(5478474)
            .publisherAddress("publisherAddress").eventType("eventType")
            .timestamp(684485648)
            .dataLocation("dataLocation")
            .relevantMetadata(List.of("metadata1", "metadata2"))
            .build();

    @Test
    void testStartProcessingEventsSuccessfulFlow() {
        // Arrange
        EventQueue eventQueue = EventQueue.builder()
                .event(List.of(dltNotification))
                .priority(EventQueuePriority.PUBLICATION_PUBLISH)
                .build();
        when(dataRetrievalQueue.getEventStream()).thenReturn(Flux.just(eventQueue));
        when(brokerEntityRetrievalService.retrieveEntityFromSourceBroker(anyString(), any(DLTNotification.class))).thenReturn(Mono.just("entityString"));
        when(brokerEntityPublisherService.publishRetrievedEntityToBroker(anyString(), anyString(), any(DLTNotification.class))).thenReturn(Mono.empty());
        // Act and Assert
        StepVerifier.create(dataRetrievalService.startRetrievingData())
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testStartProcessingEventsSuccessfulFlowRecover() {
        // Arrange
        EventQueue eventQueue = EventQueue.builder()
                .event(List.of(dltNotification, "Entity"))
                .priority(EventQueuePriority.RECOVER_PUBLISH)
                .build();
        when(dataRetrievalQueue.getEventStream()).thenReturn(Flux.just(eventQueue));
        when(brokerEntityRetrievalService.retrieveEntityFromSourceBroker(anyString(), any(DLTNotification.class))).thenReturn(Mono.just("entityString"));
        when(brokerEntityPublisherService.publishRetrievedEntityToBroker(anyString(), anyString(), any(DLTNotification.class))).thenReturn(Mono.empty());
        // Act and Assert
        StepVerifier.create(dataRetrievalService.startRetrievingData())
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }

    // TODO: Check if this test is necessary
//    @Test
//    void testRetrieveAndPublishEntityIntoBroker() {
//        // Mocking the behavior of each service in the workflow
//        when(notificationProcessorService.processDLTNotification(processId, dltNotification))
//                .thenReturn(Mono.empty());
//        when(brokerEntityRetrievalService.retrieveEntityFromSourceBroker(processId, dltNotification))
//                .thenReturn(Mono.just("entityString"));
//        when(brokerEntityPublisherService.publishRetrievedEntityToBroker(processId, "entityString", dltNotification))
//                .thenReturn(Mono.empty());
//        // Act
//        dataRetrievalService.retrieveAndPublishEntityToBroker(processId, dltNotification).block();
//        // Assert
//        verify(notificationProcessorService).processDLTNotification(processId, dltNotification);
//        verify(brokerEntityRetrievalService).retrieveEntityFromSourceBroker(processId, dltNotification);
//        verify(brokerEntityPublisherService).publishRetrievedEntityToBroker(processId, "entityString", dltNotification);
//    }

    // TODO: Check if this test is necessary
//    @Test
//    void testRetrieveAndPublishEntityIntoBrokerWithError() {
//        // Mocking the behavior to simulate an error during entity retrieval
//        when(notificationProcessorService.processDLTNotification(processId, dltNotification))
//                .thenReturn(Mono.empty());
//        when(brokerEntityRetrievalService.retrieveEntityFromSourceBroker(processId, dltNotification))
//                .thenReturn(Mono.error(new RuntimeException("Test Exception")));
//        // Act
//        synchronizer.retrieveAndPublishEntityToBroker(processId, dltNotification)
//                .onErrorResume(e -> Mono.empty())
//                .block();
//        // Assert
//        verify(notificationProcessorService).processDLTNotification(processId, dltNotification);
//        verify(brokerEntityRetrievalService).retrieveEntityFromSourceBroker(processId, dltNotification);
//        verify(brokerEntityPublisherService, never()).publishRetrievedEntityToBroker(anyString(), anyString(), any());
//    }

}
