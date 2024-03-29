//package es.in2.desmos.application.service;
//
//import es.in2.desmos.application.todo.BlockchainToBrokerDataSyncSynchronizerImpl;
//import es.in2.desmos.domain.model.DLTNotification;
//import es.in2.desmos.domain.service.BrokerEntityPublisherService;
//import es.in2.desmos.domain.service.BrokerEntityRetrievalService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class BlockchainToBrokerDataSyncSynchronizerTest {
//
//    private final String processId = "testProcessId";
//    private final DLTNotification dltNotification = DLTNotification.builder()
//            .id(5478474)
//            .publisherAddress("publisherAddress").eventType("eventType")
//            .timestamp(684485648)
//            .dataLocation("dataLocation").relevantMetadata(List.of("metadata1", "metadata2")).build();
//    @Mock
//    private NotificationProcessorService notificationProcessorService;
//    @Mock
//    private BrokerEntityRetrievalService brokerEntityRetrievalService;
//    @Mock
//    private BrokerEntityPublisherService brokerEntityPublisherService;
//    @InjectMocks
//    private BlockchainToBrokerDataSyncSynchronizerImpl synchronizer;
//
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
//        synchronizer.retrieveAndSynchronizeEntityIntoBroker(processId, dltNotification).block();
//        // Assert
//        verify(notificationProcessorService).processDLTNotification(processId, dltNotification);
//        verify(brokerEntityRetrievalService).retrieveEntityFromSourceBroker(processId, dltNotification);
//        verify(brokerEntityPublisherService).publishRetrievedEntityToBroker(processId, "entityString", dltNotification);
//    }
//
//    @Test
//    void testRetrieveAndPublishEntityIntoBrokerWithError() {
//        // Mocking the behavior to simulate an error during entity retrieval
//        when(notificationProcessorService.processDLTNotification(processId, dltNotification))
//                .thenReturn(Mono.empty());
//        when(brokerEntityRetrievalService.retrieveEntityFromSourceBroker(processId, dltNotification))
//                .thenReturn(Mono.error(new RuntimeException("Test Exception")));
//        // Act
//        synchronizer.retrieveAndSynchronizeEntityIntoBroker(processId, dltNotification)
//                .onErrorResume(e -> Mono.empty())
//                .block();
//        // Assert
//        verify(notificationProcessorService).processDLTNotification(processId, dltNotification);
//        verify(brokerEntityRetrievalService).retrieveEntityFromSourceBroker(processId, dltNotification);
//        // Verificar que la publicación no se realiza debido al error
//        verify(brokerEntityPublisherService, never()).publishRetrievedEntityToBroker(anyString(), anyString(), any());
//    }
//}
