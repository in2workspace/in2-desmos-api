package es.in2.desmos.controllers;

import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.models.BrokerNotification;
import es.in2.desmos.services.blockchain.BlockchainListenerService;
import es.in2.desmos.services.broker.BrokerListenerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private BrokerListenerService brokerListenerService;

    @Mock
    private BlockchainListenerService blockchainListenerService;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    void testPostBrokerNotification() {
        // Arrange
        BrokerNotification brokerNotification = BrokerNotification.builder()
                .id("id")
                .type("type")
                .data(List.of(Map.of("key", "value")))
                .subscriptionId("subscriptionId")
                .notifiedAt("notifiedAt")
                .build();
        Mockito.when(brokerListenerService.processBrokerNotification(anyString(), any(BrokerNotification.class)))
                .thenReturn(Mono.empty());
        // Act
        WebTestClient.bindToController(notificationController)
                .build()
                .post()
                .uri("/api/v1/notifications/broker")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(brokerNotification)
                .exchange()
                .expectStatus().isAccepted();
    }

    @Test
    void testPostDltNotification() {
        // Arrange
        BlockchainNotification blockchainNotification = BlockchainNotification.builder()
                .id(5478474)
                .publisherAddress("publisherAddress")
                .eventType("eventType")
                .timestamp(684485648)
                .dataLocation("dataLocation")
                .relevantMetadata(List.of("metadata1", "metadata2"))
                .build();
        Mockito.when(blockchainListenerService.processBlockchainNotification(anyString(), any(BlockchainNotification.class)))
                .thenReturn(Mono.empty());
        // Act
        WebTestClient.bindToController(notificationController)
                .build()
                .post()
                .uri("/api/v1/notifications/dlt")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(blockchainNotification)
                .exchange()
                .expectStatus().isAccepted();
    }

}
