package es.in2.desmos.domain.services.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import es.in2.desmos.domain.exceptions.BrokerNotificationSelfGeneratedException;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.BrokerNotification;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.api.QueueService;
import es.in2.desmos.domain.services.broker.adapter.BrokerAdapterService;
import es.in2.desmos.domain.services.broker.adapter.factory.BrokerAdapterFactory;
import es.in2.desmos.domain.services.broker.impl.BrokerListenerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrokerListenerServiceTests {

    @Mock
    private BrokerAdapterFactory brokerAdapterFactory;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AuditRecordService auditRecordService;

    @Mock
    private QueueService queueService;

    @Mock
    private BrokerAdapterService brokerAdapterService;

    @InjectMocks
    private BrokerListenerServiceImpl brokerListenerService;

    @Mock
    private ObjectWriter objectWriter;

    AuditRecord auditRecord = AuditRecord.builder()
            .id(UUID.randomUUID())
            .processId(UUID.randomUUID().toString())
            .entityId(UUID.randomUUID().toString())
            .entityType("ProductOffering")
            .entityHash("9b8685970e0de1f72c586bf5176dda04e0e6c891714f0e55a2d7745b77edfbd4")
            .entityHashLink("9b8685970e0de1f72c586bf5176dda04e0e6c891714f0e55a2d7745b77edfbd4")
            .status(AuditRecordStatus.PUBLISHED)
            .hash("hash")
            .createdAt(Timestamp.from(Instant.now()))
            .build();

    @Test
    void processBrokerNotificationTest_firstNotification() {
        // Arrange
        String processId = UUID.randomUUID().toString();
        BrokerNotification brokerNotification = BrokerNotification.builder()
                .id("notification:-5106976853901020699")
                .type("Notification")
                .data(Collections.singletonList(Map.of("id", "urn:ngsi-ld:ProductOffering:122355255",
                        "type", "ProductOffering",
                        "description", Map.of("type", "Property", "value", "Example of a Product offering for cloud services suite"),
                        "notifiedAt", "2024-04-10T11:33:43.807000Z")))
                .subscriptionId("urn:ngsi-ld:Subscription:122355255")
                .notifiedAt(Instant.now().toString())
                .build();
        // Act
        when(auditRecordService.findLatestAuditRecordForEntity(anyString(), any())).thenReturn(Mono.empty());
        when(auditRecordService.buildAndSaveAuditRecordFromBrokerNotification(anyString(), any(), any(), any())).thenReturn(Mono.empty());
        when(queueService.enqueueEvent(any())).thenReturn(Mono.empty());


        // Assert
        StepVerifier.create(brokerListenerService.processBrokerNotification(processId, brokerNotification))
                .verifyComplete();
    }

    @Test
    void processBrokerNotificationTest_SecondNotification_sameHash() throws NoSuchAlgorithmException, JsonProcessingException {
        // Arrange
        String processId = UUID.randomUUID().toString();
        BrokerNotification brokerNotification = BrokerNotification.builder()
                .id("notification:-5106976853901020699")
                .type("Notification")
                .data(Collections.singletonList(Map.of("id", "urn:ngsi-ld:ProductOffering:122355255",
                        "type", "ProductOffering",
                        "description", Map.of("type", "Property", "value", "Example of a Product offering for cloud services suite"),
                        "notifiedAt", "2024-04-10T11:33:43.807000Z")))
                .subscriptionId("urn:ngsi-ld:Subscription:122355255")
                .notifiedAt(Instant.now().toString())
                .build();
        // Act
        when(auditRecordService.findLatestAuditRecordForEntity(anyString(), any())).thenReturn(Mono.just(auditRecord));
        when(objectMapper.writer()).thenReturn(objectWriter);
        when(objectWriter.writeValueAsString(any())).thenReturn("""
                {
                  "id": "notification:-5106976853901020699",
                  "type": "Notification",
                  "data": [
                    {
                      "id": "urn:ngsi-ld:ProductOffering:122355255",
                      "type": "ProductOffering",
                      "description": {
                        "type": "Property",
                        "value": "Example of a Product offering for cloud services suite"
                      },
                      "notifiedAt": "2024-04-10T11:33:43.807Z"
                    }
                  ],
                  "subscriptionId": "urn:ngsi-ld:Subscription:122355255",
                  "notifiedAt": "2023-03-14T16:38:15.123456Z"
                }""");
        when(auditRecordService.buildAndSaveAuditRecordFromBrokerNotification(anyString(), any(), any(), any())).thenReturn(Mono.empty());

        // Assert
        StepVerifier.create(brokerListenerService.processBrokerNotification(processId, brokerNotification))
                .expectError(BrokerNotificationSelfGeneratedException.class)
                .verify();
    }

    @Test
    void processBrokerNotificationTest_SecondNotification_differentHash() throws NoSuchAlgorithmException, JsonProcessingException {
        // Arrange
        String processId = UUID.randomUUID().toString();
        BrokerNotification brokerNotification = BrokerNotification.builder()
                .id("notification:-5106976853901020699")
                .type("Notification")
                .data(Collections.singletonList(Map.of("id", "urn:ngsi-ld:ProductOffering:122355255",
                        "type", "ProductOffering",
                        "description", Map.of("type", "Property", "value", "Example of a Product offering for cloud services suite"),
                        "notifiedAt", "2024-04-10T11:33:43.807000Z")))
                .subscriptionId("urn:ngsi-ld:Subscription:122355255")
                .notifiedAt(Instant.now().toString())
                .build();
        // Act
        when(auditRecordService.findLatestAuditRecordForEntity(anyString(), any())).thenReturn(Mono.just(auditRecord));
        when(objectMapper.writer()).thenReturn(objectWriter);
        when(objectWriter.writeValueAsString(any())).thenReturn("""
                {
                  "id": "notification:-5106976853901020699",
                  "type": "Notification",
                  "data": [
                    {
                      "id": "urn:ngsi-ld:ProductOffering:122355255",
                      "type": "ProductOffering",
                      "description": {
                        "type": "Property",
                        "value": "Example of a Product offering for cloud services suites"
                      },
                      "notifiedAt": "2024-04-10T11:33:43.807Z"
                    }
                  ],
                  "subscriptionId": "urn:ngsi-ld:Subscription:122355255",
                  "notifiedAt": "2023-03-14T16:38:15.123456Z"
                }""");
        when(auditRecordService.buildAndSaveAuditRecordFromBrokerNotification(anyString(), any(), any(), any())).thenReturn(Mono.empty());
        when(queueService.enqueueEvent(any())).thenReturn(Mono.empty());


        // Assert
        StepVerifier.create(brokerListenerService.processBrokerNotification(processId, brokerNotification))
                .verifyComplete();
    }
}

