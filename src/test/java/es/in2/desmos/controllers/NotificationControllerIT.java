package es.in2.desmos.controllers;

import es.in2.desmos.ContainerManager;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.models.BrokerNotification;
import es.in2.desmos.domain.models.GlobalErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotificationControllerIT {

    @LocalServerPort
    private int localServerPort;

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    @Test
    void testInvalidBrokerNotificationControllerResponse() {
        BrokerNotification invalidNotification = BrokerNotification.builder()
                .id("")
                .type("")
                .data(null)
                .subscriptionId("")
                .notifiedAt("")
                .build();

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + localServerPort)
                .build();

        webClient.post()
                .uri("/api/v1/notifications/broker")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(invalidNotification), BrokerNotification.class)
                .exchangeToMono(clientResponse -> {
                    HttpStatusCode status = clientResponse.statusCode();
                    System.out.println("HTTP Status Code: " + status);
                    assertEquals(HttpStatus.BAD_REQUEST, status);
                    return clientResponse.bodyToMono(GlobalErrorMessage.class);
                })
                .block();
    }


    @Test
    void testInvalidBrokerNotificationExceptionHandler() {
        BrokerNotification invalidNotification = BrokerNotification.builder()
                .id("")
                .type("")
                .data(null)
                .subscriptionId("")
                .notifiedAt("")
                .build();

        GlobalErrorMessage response = WebClient.builder()
                .baseUrl("http://localhost:" + localServerPort)
                .build()
                .post()
                .uri("/api/v1/notifications/broker")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(invalidNotification), BrokerNotification.class)
                .exchangeToMono(clientResponse -> {
                    HttpStatusCode status = clientResponse.statusCode();
                    assertEquals(HttpStatus.BAD_REQUEST, status);
                    return clientResponse.bodyToMono(GlobalErrorMessage.class);
                })
                .block();

        System.out.println("Response: " + response);

        assertEquals("WebExchangeBindException", response.title());
        assertEquals("/api/v1/notifications/broker", response.path());
        assertTrue(response.message().contains("data: data cannot be null"));
        assertTrue(response.message().contains("id: id cannot be blank"));
        assertTrue(response.message().contains("subscriptionId: subscriptionId cannot be blank"));
        assertTrue(response.message().contains("type: type cannot be blank"));
        assertTrue(response.message().contains("notifiedAt: notifiedAt cannot be blank"));
    }

    @Test
    void testInvalidBlockchainNotificationControllerResponse() {
        BlockchainNotification invalidNotification = BlockchainNotification.builder()
                .id(-1)
                .publisherAddress("")
                .eventType("")
                .timestamp(0)
                .dataLocation("")
                .relevantMetadata(null)
                .entityId("")
                .previousEntityHash("")
                .build();

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + localServerPort)
                .build();

        webClient.post()
                .uri("/api/v1/notifications/dlt")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(invalidNotification), BlockchainNotification.class)
                .exchangeToMono(clientResponse -> {
                    HttpStatusCode status = clientResponse.statusCode();
                    System.out.println("HTTP Status Code: " + status);
                    assertEquals(HttpStatus.BAD_REQUEST, status);
                    return clientResponse.bodyToMono(GlobalErrorMessage.class);
                })
                .block();
    }


    @Test
    void testInvalidBlockchainNotificationExceptionHandler() {
        BlockchainNotification invalidNotification = BlockchainNotification.builder()
                .id(-1)
                .publisherAddress("")
                .eventType("")
                .timestamp(0)
                .dataLocation("")
                .relevantMetadata(null)
                .entityId("")
                .previousEntityHash("")
                .build();

        GlobalErrorMessage response = WebClient.builder()
                .baseUrl("http://localhost:" + localServerPort)
                .build()
                .post()
                .uri("/api/v1/notifications/dlt")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(invalidNotification), BlockchainNotification.class)
                .exchangeToMono(clientResponse -> {
                    HttpStatusCode status = clientResponse.statusCode();
                    assertEquals(HttpStatus.BAD_REQUEST, status);
                    return clientResponse.bodyToMono(GlobalErrorMessage.class);
                })
                .block();

        System.out.println("Response: " + response);

        assertEquals("WebExchangeBindException", response.title());
        assertEquals("/api/v1/notifications/dlt", response.path());
        assertTrue(response.message().contains("id must be positive or zero"));
        assertTrue(response.message().contains("publisherAddress must not be blank"));
        assertTrue(response.message().contains("eventType must not be blank"));
//        assertTrue(response.message().contains("timestamp must not be null"));
        assertTrue(response.message().contains("dataLocation must not be blank"));
        assertTrue(response.message().contains("relevantMetadata must not be null"));
        assertTrue(response.message().contains("entityIDHash must not be blank"));
        assertTrue(response.message().contains("previousEntityHash must not be blank"));
    }

}