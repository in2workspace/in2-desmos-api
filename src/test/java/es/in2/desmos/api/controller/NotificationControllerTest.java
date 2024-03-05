package es.in2.desmos.api.controller;

import es.in2.desmos.api.facade.BlockchainToBrokerSynchronizer;
import es.in2.desmos.api.facade.BrokerToBlockchainPublisher;
import es.in2.desmos.api.model.BlockchainNotification;
import es.in2.desmos.api.model.BrokerNotification;
import es.in2.desmos.api.model.EventQueuePriority;
import static org.mockito.Mockito.*;
import es.in2.desmos.api.service.NotificationProcessorService;
import es.in2.desmos.api.service.QueueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private BrokerToBlockchainPublisher brokerToBlockchainPublisher;

    @Mock
    private BlockchainToBrokerSynchronizer blockchainToBrokerSynchronizer;

    @Mock
    private QueueService blockchainToBrokerQueueService;

    @Mock
    private QueueService brokerToBlockchainQueueService;

    @Mock
    private NotificationProcessorService notificationProcessorService;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    void testCaptureBrokerNotification() {
        // Arrange
        BrokerNotification brokerNotification = BrokerNotification.builder()
                .id("id")
                .type("type")
                .data(List.of(Map.of("key", "value")))
                .subscriptionId("subscriptionId")
                .notifiedAt("notifiedAt")
                .build();

        Mockito.when(notificationProcessorService.detectBrokerNotificationPriority(anyString(), any(BrokerNotification.class)))
                .thenReturn(Mono.empty());

        // Act
        WebTestClient.bindToController(notificationController)
                .build()
                .post()
                .uri("/notifications/broker")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(brokerNotification)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testDltNotification() {
        // Arrange
        BlockchainNotification blockchainNotification = BlockchainNotification.builder()
                .id(5478474)
                .publisherAddress("publisherAddress")
                .eventType("eventType")
                .timestamp(684485648)
                .dataLocation("dataLocation")
                .relevantMetadata(List.of("metadata1", "metadata2"))
                .build();

        Mockito.when(notificationProcessorService.detectBlockchainNotificationPriority(anyString(), any(BlockchainNotification.class)))
                .thenReturn(Mono.empty());
        // Act
        WebTestClient.bindToController(notificationController)
                .build()
                .post()
                .uri("/notifications/dlt")
                .bodyValue(blockchainNotification)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK);
    }

}