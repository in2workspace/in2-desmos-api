package es.in2.desmos.infrastructure.controller;

import es.in2.desmos.z.services.NotificationProcessorService;
import es.in2.desmos.domain.model.BrokerNotification;
import es.in2.desmos.domain.model.BlockchainNotification;
import es.in2.desmos.controllers.NotificationController;
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
import static org.mockito.Mockito.anyString;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationProcessorService notificationProcessorService;

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
        Mockito.when(notificationProcessorService.processBrokerNotification(anyString(), any(BrokerNotification.class)))
                .thenReturn(Mono.empty());
        // Act
        WebTestClient.bindToController(notificationController)
                .build()
                .post()
                .uri("/api/v1/notifications/broker")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(brokerNotification)
                .exchange()
                .expectStatus().isOk();
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
        Mockito.when(notificationProcessorService.processDLTNotification(anyString(), any(BlockchainNotification.class)))
                .thenReturn(Mono.empty());
        // Act
        WebTestClient.bindToController(notificationController)
                .build()
                .post()
                .uri("/api/v1/notifications/dlt")
                .bodyValue(blockchainNotification)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK);
    }

}
