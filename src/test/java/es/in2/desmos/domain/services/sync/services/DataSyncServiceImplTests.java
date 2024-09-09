package es.in2.desmos.domain.services.sync.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exceptions.BrokerEntityRetrievalException;
import es.in2.desmos.domain.exceptions.HashLinkException;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.sync.services.impl.DataSyncServiceImpl;
import es.in2.desmos.infrastructure.configs.ApiConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSyncServiceImplTests {

    BlockchainNotification notification = BlockchainNotification.builder()
            .id(2240)
            .publisherAddress("0x40b0ab9dfd960064fb7e9fdf77f889c71569e349055ff563e8d699d8fa97fa90")
            .eventType("ProductOffering")
            .timestamp(1712753824)
            .dataLocation("http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:ProductOffering:122355255?hl=6d91b01418c21ccad12072d5f986bab2c99206bb08e65e5a430a35f7e60dcdbf")
            .relevantMetadata(Collections.emptyList())
            .entityId("0x4eb401aa1248b6a95c298d0747eb470b6ba6fc3f54ea630dc6c77f23ad1abe3e")
            .previousEntityHashLink("0x6d91b01418c21ccad12072d5f986bab2c99206bb08e65e5a430a35f7e60dcdbf")
            .build();
    AuditRecord auditRecord = AuditRecord.builder()
            .id(UUID.randomUUID())
            .processId(UUID.randomUUID().toString())
            .entityId(UUID.randomUUID().toString())
            .entityType("ProductOffering")
            .entityHashLink("6d91b01418c21ccad12072d5f986bab2c99206bb08e65e5a430a35f7e60dcdbf")
            .status(AuditRecordStatus.PUBLISHED)
            .createdAt(Timestamp.from(Instant.now()))
            .build();
    AuditRecord errorAuditRecord = AuditRecord.builder()
            .id(UUID.randomUUID())
            .processId(UUID.randomUUID().toString())
            .entityId(UUID.randomUUID().toString())
            .entityType("ProductOffering")
            .entityHashLink("fcb394bb4f2da4abbf53ab7e4898418257b7c6abe0c110f466f3ee947d057e579") // Wrong entityHashLink
            .status(AuditRecordStatus.PUBLISHED)
            .createdAt(Timestamp.from(Instant.now()))
            .build();
    BlockchainNotification errorNotification = BlockchainNotification.builder()
            .id(2240)
            .publisherAddress("0x40b0ab9dfd960064fb7e9fdf77f889c71569e349055ff563e8d699d8fa97fa90")
            .eventType("ProductOffering")
            .timestamp(1712753824)
            .dataLocation("http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:ProductOffering:122355255?hl=fcb394bb4f2da4abbf53ab7eb9b5b8257b7c6abe6c110f466f3ee947d057e579")
            .relevantMetadata(Collections.emptyList())
            .entityId("0x4eb401aa1248b6a95c298d0747eb470b6ba6fc3f54ea630dc6c77f23ad1abe3e")
            .previousEntityHashLink("0xfcb394bb4f2da4abbf53ab7eb9b5b8257b7c6abe0c110f468f3ee947d057e579")
            .build();
    String retrievedBrokerEntityMock = """
            {
                "id": "urn:ngsi-ld:ProductOffering:122355255",
                "type": "ProductOffering",
                "name": {
                    "type": "Property",
                    "value": "ProductOffering 1"
                },
                "description": {
                    "type": "Property",
                    "value": "ProductOffering 1 description"
                }
            }""";
    @Mock
    WebClient.RequestHeadersUriSpec webClientRequestHeadersUriSpecMock;
    @Mock
    WebClient.RequestHeadersSpec webClientRequestHeadersSpecMock;
    @Mock
    WebClient.ResponseSpec webClientResponseSpecMock;
    @Mock
    private ApiConfig apiConfig;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private AuditRecordService auditRecordService;
    @Mock
    private WebClient webClientMock;
    @InjectMocks
    private DataSyncServiceImpl dataSyncService;

    @Test
    void testVerifyDataIntegrity_Success_FirstEntity() {
        //Arrange
        when(auditRecordService.findLatestConsumerPublishedAuditRecordByEntityId(anyString(), anyString())).thenReturn(Mono.empty());

        //Act & Assert
        StepVerifier.create(dataSyncService.verifyRetrievedEntityData("processId", notification, retrievedBrokerEntityMock))
                .assertNext(retrievedBrokerEntity -> {
                })
                .verifyComplete();
    }

    @Test
    void testVerifyDataIntegrityAndDataConsistency_Success() {
        //Arrange
        when(auditRecordService.findLatestConsumerPublishedAuditRecordByEntityId(anyString(), anyString())).thenReturn(Mono.just(auditRecord));

        //Act & Assert
        StepVerifier.create(dataSyncService.verifyRetrievedEntityData("processId", notification, retrievedBrokerEntityMock))
                .assertNext(retrievedBrokerEntity -> {
                })
                .verifyComplete();

    }

    @Test
    void testVerifyDataIntegrityAndDataConsistency_Failure() {
        //Arrange
        when(auditRecordService.findLatestConsumerPublishedAuditRecordByEntityId(anyString(), anyString())).thenReturn(Mono.just(errorAuditRecord));

        //Act & Assert
        StepVerifier.create(dataSyncService.verifyRetrievedEntityData("processId", notification, retrievedBrokerEntityMock))
                .expectError(HashLinkException.class)
                .verify();

    }

    @Test
    void testVerifyDataIntegrity_Failure_HashMismatch() {
        //Arrange
        when(auditRecordService.findLatestConsumerPublishedAuditRecordByEntityId(anyString(), anyString())).thenReturn(Mono.empty());

        //Act & Assert
        StepVerifier.create(dataSyncService.verifyRetrievedEntityData("processId", errorNotification, retrievedBrokerEntityMock))
                .expectError(HashLinkException.class)
                .verify();
    }

    @Test
    void getEntityFromExternalSource() {
        //Arrange
        String mockResponse = "{ \"id\": \"urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69\" }";
        Mono<String> monoMockResponse = Mono.just(mockResponse);

        when(apiConfig.webClient()).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(webClientRequestHeadersUriSpecMock);
        when(webClientRequestHeadersUriSpecMock.uri(anyString())).thenReturn(webClientRequestHeadersSpecMock);
        when(webClientRequestHeadersSpecMock.accept(any(MediaType.class))).thenReturn(webClientRequestHeadersSpecMock);
        when(webClientRequestHeadersSpecMock.header(anyString(), anyString())).thenReturn(webClientRequestHeadersSpecMock);
        when(webClientRequestHeadersSpecMock.retrieve()).thenReturn(webClientResponseSpecMock);
        when(webClientResponseSpecMock.onStatus(any(), any())).thenReturn(webClientResponseSpecMock);
        when(webClientResponseSpecMock.bodyToMono(String.class)).thenReturn(monoMockResponse);

        //Act
        Mono<String> result = dataSyncService.getEntityFromExternalSource("processId", notification);

        //Assert
        StepVerifier.create(result)
                .expectNextMatches(entity -> {
                    return entity.contains("urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69");
                })
                .verifyComplete();
    }

    @Test
    void getEntityFromExternalSource_WhenStatusIs200() {
        //Arrange
        String mockResponse = "{ \"id\": \"urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69\" }";
        Mono<String> monoMockResponse = Mono.just(mockResponse);

        when(apiConfig.webClient()).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(webClientRequestHeadersUriSpecMock);
        when(webClientRequestHeadersUriSpecMock.uri(anyString())).thenReturn(webClientRequestHeadersSpecMock);
        when(webClientRequestHeadersSpecMock.accept(any(MediaType.class))).thenReturn(webClientRequestHeadersSpecMock);
        when(webClientRequestHeadersSpecMock.header(anyString(), anyString())).thenReturn(webClientRequestHeadersSpecMock);
        when(webClientRequestHeadersSpecMock.retrieve()).thenReturn(webClientResponseSpecMock);
        when(webClientResponseSpecMock.onStatus(any(), any())).thenReturn(webClientResponseSpecMock);
        when(webClientResponseSpecMock.bodyToMono(String.class)).thenReturn(monoMockResponse);
        when(webClientResponseSpecMock.onStatus(argThat(predicate -> predicate.test(HttpStatus.OK)), any())).thenAnswer(invocation -> {

            Function<ClientResponse, Mono<Void>> function = invocation.getArgument(1);

            function.apply(mock(ClientResponse.class));

            return webClientResponseSpecMock;
        });

        //Act
        Mono<String> result = dataSyncService.getEntityFromExternalSource("processId", notification);

        //Assert
        StepVerifier.create(result)
                .expectNext(mockResponse)
                .verifyComplete();

        verify(webClientResponseSpecMock, times(3)).onStatus(any(), any());
    }

    @Test
    void getEntityFromExternalSource_WhenStatusIs4xx() {
        //Arrange
        when(apiConfig.webClient()).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(webClientRequestHeadersUriSpecMock);
        when(webClientRequestHeadersUriSpecMock.uri(anyString())).thenReturn(webClientRequestHeadersSpecMock);
        when(webClientRequestHeadersSpecMock.accept(any(MediaType.class))).thenReturn(webClientRequestHeadersSpecMock);
        when(webClientRequestHeadersSpecMock.header(anyString(), anyString())).thenReturn(webClientRequestHeadersSpecMock);
        when(webClientRequestHeadersSpecMock.retrieve()).thenReturn(webClientResponseSpecMock);
        when(webClientResponseSpecMock.onStatus(any(), any())).thenReturn(webClientResponseSpecMock);
        when(webClientResponseSpecMock.onStatus(argThat(predicate -> predicate.test(HttpStatus.BAD_REQUEST)), any())).thenAnswer(invocation -> {
            Function<ClientResponse, Mono<? extends Throwable>> function = invocation.getArgument(1);

            assertThrows(BrokerEntityRetrievalException.class, () -> function.apply(mock(ClientResponse.class)));

            return webClientResponseSpecMock;
        });

        when(webClientResponseSpecMock.bodyToMono(String.class)).thenThrow(new BrokerEntityRetrievalException("Error occurred while retrieving entity from the external broker"));

        //Act & Assert
        assertThrows(BrokerEntityRetrievalException.class, () -> {
            dataSyncService.getEntityFromExternalSource("processId", notification);
        });
    }

    @Test
    void getEntityFromExternalSource_WhenStatusIs5xx() {
        //Arrange
        when(apiConfig.webClient()).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(webClientRequestHeadersUriSpecMock);
        when(webClientRequestHeadersUriSpecMock.uri(anyString())).thenReturn(webClientRequestHeadersSpecMock);
        when(webClientRequestHeadersSpecMock.accept(any(MediaType.class))).thenReturn(webClientRequestHeadersSpecMock);
        when(webClientRequestHeadersSpecMock.header(anyString(), anyString())).thenReturn(webClientRequestHeadersSpecMock);
        when(webClientRequestHeadersSpecMock.retrieve()).thenReturn(webClientResponseSpecMock);
        when(webClientResponseSpecMock.onStatus(any(), any())).thenReturn(webClientResponseSpecMock);
        when(webClientResponseSpecMock.onStatus(argThat(predicate -> predicate.test(HttpStatus.INTERNAL_SERVER_ERROR)), any())).thenAnswer(invocation -> {
            Function<ClientResponse, Mono<? extends Throwable>> function = invocation.getArgument(1);

            assertThrows(BrokerEntityRetrievalException.class, () -> function.apply(mock(ClientResponse.class)));

            return webClientResponseSpecMock;
        });

        when(webClientResponseSpecMock.bodyToMono(String.class)).thenThrow(new BrokerEntityRetrievalException("Error occurred while retrieving entity from the external broker"));

        //Act & Assert
        assertThrows(BrokerEntityRetrievalException.class, () -> {
            dataSyncService.getEntityFromExternalSource("processId", notification);
        });
    }

}
