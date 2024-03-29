package es.in2.desmos.application.service;

import es.in2.desmos.application.service.impl.DataPublicationServiceImpl;
import es.in2.desmos.domain.model.DLTEvent;
import es.in2.desmos.domain.model.BrokerNotification;
import es.in2.desmos.domain.model.EventQueue;
import es.in2.desmos.domain.model.EventQueuePriority;
import es.in2.desmos.domain.service.AuditRecordService;
import es.in2.desmos.domain.service.DLTEventCreatorService;
import es.in2.desmos.domain.service.QueueService;
import es.in2.desmos.infrastructure.blockchain.service.DLTAdapterPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataPublicationServiceTest {

    @Mock
    private QueueService dataPublicationQueue;
    @Mock
    private DLTEventCreatorService dltEventCreatorService;
    @Mock
    private DLTAdapterPublisher dltAdapterPublisher;
    @Mock
    private AuditRecordService auditRecordService;
    @InjectMocks
    private DataPublicationServiceImpl dataPublicationService;

//    @Test
//    void testStartPublishingDataToDLT_SuccessfulFlow() {
//        // Arrange
//        String processId = UUID.randomUUID().toString();
//        BrokerNotification brokerNotification = new BrokerNotification("id", "type", Collections.singletonList(Map.of("key", "value")), "subscriptionId", "notifiedAt");
//        EventQueue eventQueue = EventQueue.builder().event(Collections.singletonList(brokerNotification)).priority(EventQueuePriority.PUBLICATION_PUBLISH).build();
//        DLTEvent dltEvent = new DLTEvent("eventType", "organizationId", "entityId", "previousEntityHash", "dataLocation", Collections.emptyList());
//
//        when(dataPublicationQueue.getEventStream()).thenReturn(Flux.just(eventQueue));
//        when(auditRecordService.fetchLatestProducerEntityHashByEntityId(anyString(), anyString())).thenReturn(Mono.just("previousEntityHash"));
//        when(dltEventCreatorService.buildDLTEvent(anyString(), anyMap(), anyString())).thenReturn(Mono.just(dltEvent));
//        when(dltAdapterPublisher.publishBlockchainEvent(anyString(), any(DLTEvent.class))).thenReturn(Mono.empty());
//
//        // Act & Assert
//        StepVerifier.create(dataPublicationService.startPublishingDataToDLT())
//                .expectSubscription()
//                .expectNextCount(0) // Since the method does not emit any items but completes successfully
//                .verifyComplete();
//
//        // The verifications are implicit in the behavior of StepVerifier.
//        // For more explicit verification, you could track interactions with mocks.
//    }

    // TODO Recover test, not yet implemented
//    @Test
//    void testStartProcessingEventsSuccessfulFlow_recover() {
//        // Arrange
//        EventQueue eventQueue = EventQueue.builder().event(List.of(blockchainEvent)).priority(EventQueuePriority.RECOVER_PUBLISH).build();
//        when(dataPublicationQueue.getEventStream()).thenReturn(Flux.just(eventQueue));
//        when(dltAdapterPublisher.publishBlockchainEvent(anyString(), any(BlockchainEvent.class))).thenReturn(Mono.empty());
//        // Act and Assert
//        StepVerifier.create(dataPublicationService.startPublishingDataToDLT())
//                .expectSubscription()
//                .expectNextCount(0)
//                .verifyComplete();
//    }

    // TODO: Check if this test is necessary
//    @Test
//    void testProcessAndPublishBrokerNotificationToBlockchain_Success() {
//        // Arrange
//        HashMap<String, Object> dataMap = new HashMap<>();
//        // Mock the behavior of services
//        when(notificationProcessorService.processBrokerNotification(anyString(), any()))
//                .thenReturn(Mono.empty());
//        when(DLTEventCreatorService.createBlockchainEvent(anyString(), any()))
//                .thenReturn(Mono.just(blockchainEvent));
//        when(dltAdapterPublisher.publishBlockchainEvent(anyString(), any()))
//                .thenReturn(Mono.empty());
//        // Act
//        brokerToBlockchainPublisher.processAndPublishBrokerNotificationToBlockchain(processId, brokerNotification).block();
//        // Assert - Verify that services are called with the expected parameters
//        verify(notificationProcessorService).processBrokerNotification(processId, brokerNotification);
//        verify(DLTEventCreatorService).createBlockchainEvent(processId, dataMap);
//        verify(dltAdapterPublisher).publishBlockchainEvent(processId, blockchainEvent);
//    }

    // TODO: Check if this test is necessary
//    @Test
//    void testProcessAndPublishBrokerNotificationToBlockchain_Error() {
//        // Simulate an error in the NotificationProcessorService
//        when(notificationProcessorService.processBrokerNotification(anyString(), any()))
//                .thenReturn(Mono.error(new RuntimeException("Test Processing Error")));
//        // No need to mock other services as the error will prevent their execution
//        // Execute the method and handle the error to prevent test failure
//        brokerToBlockchainPublisher.processAndPublishBrokerNotificationToBlockchain(processId, brokerNotification)
//                .onErrorResume(e -> Mono.empty()) // Handle error to prevent test failure
//                .block();
//        // Verify that the first service is called and others are not due to the error
//        verify(notificationProcessorService).processBrokerNotification(processId, brokerNotification);
//        verifyNoInteractions(DLTEventCreatorService);
//        verifyNoInteractions(dltAdapterPublisher);
//        // Verify error logging - This part is tricky as Mockito doesn't directly support verifying log statements.
//        // You may need to use additional tools or frameworks to assert log output, or alternatively,
//        // verify that the subsequent steps after the error are not executed, as done here.
//    }

}
