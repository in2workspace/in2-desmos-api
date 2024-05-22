package es.in2.desmos.infrastructure.controllers;

import es.in2.desmos.domain.services.sync.services.DataSyncService;
import es.in2.desmos.infrastructure.controllers.DataSyncController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class DataSyncControllerTests {

    @Mock
    private DataSyncService dataSyncService;

    @InjectMocks
    private DataSyncController dataSyncController;

    @Test
    void testSyncData() {
        // Arrange
        Mockito.when(dataSyncService.synchronizeData(anyString())).thenReturn(Mono.empty());
        // Act
        WebTestClient.bindToController(dataSyncController)
                .build()
                .get()
                .uri("/api/v1/sync/data")
                .exchange()
                .expectStatus().isOk();
    }

}
