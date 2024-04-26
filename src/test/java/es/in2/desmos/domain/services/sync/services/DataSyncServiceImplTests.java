package es.in2.desmos.domain.services.sync.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.configs.ApiConfig;
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
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataSyncServiceImplTests {

    @Mock
    private ApiConfig apiConfig;

    @Mock
    private ObjectMapper objectMapper;

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


        StepVerifier.create(dataSyncService.verifyRetrievedEntityData("processId", notification, retrievedBrokerEntity))
                .verifyComplete();
    }

    @Test
    void testVerifyDataIntegrity_Failure_HashMismatch() throws JsonProcessingException {
        JsonNode mockJsonNode = Mockito.mock(JsonNode.class);
        when(objectMapper.readTree(anyString())).thenReturn(mockJsonNode);
        when(objectMapper.writeValueAsString(mockJsonNode)).thenReturn(retrievedBrokerEntity);


        StepVerifier.create(dataSyncService.verifyRetrievedEntityData("processId", errorNotification, retrievedBrokerEntity))
                .expectError(HashLinkException.class)
                .verify();
    }
}
