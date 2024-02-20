package es.in2.desmos.api.facade;

import es.in2.desmos.api.facade.impl.BrokerToBlockchainDataSyncPublisherImpl;
import es.in2.desmos.api.model.BlockchainEvent;
import es.in2.desmos.api.model.BrokerNotification;
import es.in2.desmos.api.service.BlockchainEventCreatorService;
import es.in2.desmos.api.service.BrokerEntityProcessorService;
import es.in2.desmos.blockchain.service.DLTAdapterEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)

 class BrokerToBlockchainDataSyncPublisherTest {

    // Test data
    private final String processId = "testProcessId";
    private final BrokerNotification brokerNotification = BrokerNotification.builder().id("id")
            .type("type").data(List.of(Map.of("key", "value"))).subscriptionId("subscriptionId")
            .notifiedAt("notifiedAt").build();
    private final BlockchainEvent blockchainEvent = BlockchainEvent.builder()
            .eventType("eventType").organizationId("organizationId").entityId("entityId")
            .previousEntityHash("previousEntityHash").dataLocation("dataLocation")
            .metadata(List.of("metadata1", "metadata2")).build();
    @Mock
    private BrokerEntityProcessorService brokerEntityProcessorService;
    @Mock
    private BlockchainEventCreatorService blockchainEventCreatorService;
    @Mock
    private DLTAdapterEventPublisher DLTAdapterEventPublisher;
    @InjectMocks
    private BrokerToBlockchainDataSyncPublisherImpl brokerToBlockchainPublisher;

    @Test
    void testProcessAndPublishBrokerNotificationToBlockchain_Success() {
        // Arrange
        HashMap<String, Object> dataMap = new HashMap<>();
        // Mock the behavior of services
        when(brokerEntityProcessorService.processBrokerEntity(anyString(), any()))
                .thenReturn(Mono.just(dataMap));
        when(blockchainEventCreatorService.createBlockchainEvent(anyString(), any()))
                .thenReturn(Mono.just(blockchainEvent));
        when(DLTAdapterEventPublisher.publishBlockchainEvent(anyString(), any()))
                .thenReturn(Mono.empty());
        // Act
        brokerToBlockchainPublisher.createAndSynchronizeBlockchainEvents(processId, brokerNotification.id()).block();
        // Assert - Verify that services are called with the expected parameters
        verify(brokerEntityProcessorService).processBrokerEntity(processId, brokerNotification.id());
        verify(blockchainEventCreatorService).createBlockchainEvent(processId, dataMap);
        verify(DLTAdapterEventPublisher).publishBlockchainEvent(processId, blockchainEvent);
    }

    @Test
    void testProcessAndPublishBrokerNotificationToBlockchain_Error() {
        // Simulate an error in the NotificationProcessorService
        when(brokerEntityProcessorService.processBrokerEntity(anyString(), any()))
                .thenReturn(Mono.error(new RuntimeException("Test Processing Error")));
        // No need to mock other services as the error will prevent their execution
        // Execute the method and handle the error to prevent test failure
        brokerToBlockchainPublisher.createAndSynchronizeBlockchainEvents(processId, brokerNotification.id())
                .onErrorResume(e -> Mono.empty()) // Handle error to prevent test failure
                .block();
        // Verify that the first service is called and others are not due to the error
        verify(brokerEntityProcessorService).processBrokerEntity(processId, brokerNotification.id());
        verifyNoInteractions(blockchainEventCreatorService);
        verifyNoInteractions(DLTAdapterEventPublisher);
        // Verify error logging - This part is tricky as Mockito doesn't directly support verifying log statements.
        // You may need to use additional tools or frameworks to assert log output, or alternatively,
        // verify that the subsequent steps after the error are not executed, as done here.
    }
}
