package es.in2.desmos.domain.services.sync.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.infrastructure.configs.ApiConfig;
import es.in2.desmos.domain.exceptions.HashLinkException;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.services.sync.services.impl.DataSyncServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataSyncServiceImplTests {

    @Mock
    private ApiConfig apiConfig;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AuditRecordService auditRecordService;

    @InjectMocks
    private DataSyncServiceImpl dataSyncService;

    BlockchainNotification notification = BlockchainNotification.builder()
            .id(2240)
            .publisherAddress("0x40b0ab9dfd960064fb7e9fdf77f889c71569e349055ff563e8d699d8fa97fa90")
            .eventType("ProductOffering")
            .timestamp(1712753824)
            .dataLocation("http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:ProductOffering:122355255?hl=fcb394bb4f2da4abbf53ab7eb9b5b8257b7c6abe0c110f466f3ee947d057e579")
            .relevantMetadata(Collections.emptyList())
            .entityId("0x4eb401aa1248b6a95c298d0747eb470b6ba6fc3f54ea630dc6c77f23ad1abe3e")
            .previousEntityHash("0xfcb394bb4f2da4abbf53ab7eb9b5b8257b7c6abe0c110f466f3ee947d057e579")
            .build();

    AuditRecord auditRecord = AuditRecord.builder()
            .id(UUID.randomUUID())
            .processId(UUID.randomUUID().toString())
            .entityId(UUID.randomUUID().toString())
            .entityType("ProductOffering")
            .entityHashLink("fcb394bb4f2da4abbf53ab7eb9b5b8257b7c6abe0c110f466f3ee947d057e579")
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
            .previousEntityHash("0xfcb394bb4f2da4abbf53ab7eb9b5b8257b7c6abe0c110f468f3ee947d057e579")
            .build();


    String retrievedBrokerEntity = """
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

    @BeforeEach
    void setup() throws JsonProcessingException {
    }

    @Test
    void testVerifyDataIntegrity_Success_FirstEntity() throws JsonProcessingException {
        JsonNode mockJsonNode = Mockito.mock(JsonNode.class);
        when(objectMapper.readTree(anyString())).thenReturn(mockJsonNode);
        when(objectMapper.writeValueAsString(mockJsonNode)).thenReturn(retrievedBrokerEntity);
        when(auditRecordService.findLatestConsumerPublishedAuditRecord(anyString())).thenReturn(Flux.empty());


        StepVerifier.create(dataSyncService.verifyRetrievedEntityData("processId", notification, retrievedBrokerEntity))
                .assertNext(retrievedBrokerEntity -> {})
                .verifyComplete();
    }

    @Test
    void testVerifyDataIntegrity_Success_SecondEntity() throws JsonProcessingException {
        JsonNode mockJsonNode = Mockito.mock(JsonNode.class);
        when(objectMapper.readTree(anyString())).thenReturn(mockJsonNode);
        when(objectMapper.writeValueAsString(mockJsonNode)).thenReturn(retrievedBrokerEntity);
        when(auditRecordService.findLatestConsumerPublishedAuditRecord(anyString())).thenReturn(Flux.just(auditRecord));

        StepVerifier.create(dataSyncService.verifyRetrievedEntityData("processId", notification, retrievedBrokerEntity))
                .assertNext(retrievedBrokerEntity -> {})
                .verifyComplete();

    }

    @Test
    void testVerifyDataIntegrity_Failure_HashMismatch() throws JsonProcessingException {
        JsonNode mockJsonNode = Mockito.mock(JsonNode.class);
        when(objectMapper.readTree(anyString())).thenReturn(mockJsonNode);
        when(objectMapper.writeValueAsString(mockJsonNode)).thenReturn(retrievedBrokerEntity);
        when(auditRecordService.findLatestConsumerPublishedAuditRecord(anyString())).thenReturn(Flux.empty());


        StepVerifier.create(dataSyncService.verifyRetrievedEntityData("processId", errorNotification, retrievedBrokerEntity))
                .expectError(HashLinkException.class)
                .verify();
    }

    @Test
    void testVerifyDataIntegrity_JsonProcessingExceptionError() throws JsonProcessingException {
        JsonNode mockJsonNode = Mockito.mock(JsonNode.class);
        when(objectMapper.readTree(anyString())).thenReturn(mockJsonNode);
        when(objectMapper.writeValueAsString(mockJsonNode)).thenThrow(JsonProcessingException.class);
        when(auditRecordService.findLatestConsumerPublishedAuditRecord(anyString())).thenReturn(Flux.empty());


        StepVerifier.create(dataSyncService.verifyRetrievedEntityData("processId", errorNotification, retrievedBrokerEntity))
                .expectError(HashLinkException.class)
                .verify();
    }
}
