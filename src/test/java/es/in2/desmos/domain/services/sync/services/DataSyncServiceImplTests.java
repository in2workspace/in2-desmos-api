package es.in2.desmos.domain.services.sync.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exceptions.HashLinkException;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.sync.services.impl.DataSyncServiceImpl;
import es.in2.desmos.infrastructure.configs.ApiConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataSyncServiceImplTests {

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
    @Mock
    private ApiConfig apiConfig;
    @Spy
    private ObjectMapper objectMapper;
    @Mock
    private AuditRecordService auditRecordService;
    @InjectMocks
    private DataSyncServiceImpl dataSyncService;

    private static Stream<Arguments> provideTestData() {
        String ObjectInput = """
                {
                    "name": {
                        "type": "Property",
                        "value": "ProductOffering 1"
                    },
                    "id": "urn:ngsi-ld:ProductOffering:122355255",
                    "description": {
                        "type": "Property",
                        "value": "ProductOffering 1 description"
                    },
                    "type": "ProductOffering"
                }
                """;
        String ObjectInputExpected = """
                {
                    "description": {
                        "type": "Property",
                        "value": "ProductOffering 1 description"
                    },
                    "id": "urn:ngsi-ld:ProductOffering:122355255",
                    "name": {
                        "type": "Property",
                        "value": "ProductOffering 1"
                    },
                    "type": "ProductOffering"
                }
                """;

        String arrayInput = """
                [
                    {
                       "id": "urn:ngsi-ld:ProductOffering:122355256",
                        "description": {
                            "type": "Property",
                            "value": "ProductOffering 2 description"
                        },
                        "type": "ProductOffering",
                        "name": {
                            "type": "Property",
                            "value": "ProductOffering 2"
                        }
                    }
                ]
                """;

        String arrayInputExpected = """
                [
                    {
                        "description": {
                            "type": "Property",
                            "value": "ProductOffering 2 description"
                        },
                        "id": "urn:ngsi-ld:ProductOffering:122355256",
                        "name": {
                            "type": "Property",
                            "value": "ProductOffering 2"
                        },
                        "type": "ProductOffering"
                    }
                ]
                """;

        String arrayInput2 = """
                    [
                    {
                        "nombre": "Objeto 1",
                        "id": 1,
                        "descripcion": {
                            "tamaño": "grande",
                            "color": "rojo"
                        },
                        "subelementos": [
                            {
                                "nombre": "Objeto 4",
                                "id": 4,
                                "descripcion": {
                                    "tamaño": "mediano",
                                    "color": "naranja"
                                }
                            },
                            {
                                "nombre": "Objeto 5",
                                "id": 5,
                                "descripcion": {
                                    "tamaño": "pequeño",
                                    "color": "azul"
                                }
                            }
                        ]
                    },
                    {
                        "nombre": "Objeto 2",
                        "id": 2,
                        "descripcion": {
                            "tamaño": "pequeño",
                            "color": "azul"
                        }
                    }
                ]
                """;

        String arrayInput2Expected = """
                [
                    {
                        "descripcion": {
                            "color": "rojo",
                            "tamaño": "grande"
                        },
                        "id": 1,
                        "nombre": "Objeto 1",
                        "subelementos": [
                            {
                                "descripcion": {
                                    "color": "naranja",
                                    "tamaño": "mediano"
                                },
                                "id": 4,
                                "nombre": "Objeto 4"
                            },
                            {
                                "descripcion": {
                                    "color": "azul",
                                    "tamaño": "pequeño"
                                },
                                "id": 5,
                                "nombre": "Objeto 5"
                            }
                        ]
                    },
                    {
                        "descripcion": {
                            "color": "azul",
                            "tamaño": "pequeño"
                        },
                        "id": 2,
                        "nombre": "Objeto 2"
                    }
                ]
                """;

        String primitiveInput = "\"ProductOffering 3\"";
        String primitiveInputExpected = "\"ProductOffering 3\"";

        return Stream.of(
                Arguments.of(ObjectInput, ObjectInputExpected),
                Arguments.of(arrayInput, arrayInputExpected),
                Arguments.of(primitiveInput, primitiveInputExpected),
                Arguments.of(arrayInput2, arrayInput2Expected));
    }

    @BeforeEach
    void setup() throws JsonProcessingException {
    }

    @Test
    void testVerifyDataIntegrity_Success_FirstEntity() throws JsonProcessingException {
        JsonNode mockJsonNode = Mockito.mock(JsonNode.class);
        when(objectMapper.readTree(anyString())).thenReturn(mockJsonNode);
        when(objectMapper.writeValueAsString(mockJsonNode)).thenReturn(retrievedBrokerEntity);
        when(auditRecordService.findLatestConsumerPublishedAuditRecordByEntityId(anyString(), anyString())).thenReturn(Mono.empty());


        StepVerifier.create(dataSyncService.verifyRetrievedEntityData("processId", notification, retrievedBrokerEntity))
                .assertNext(retrievedBrokerEntity -> {
                })
                .verifyComplete();
    }

    @Test
    void testVerifyDataIntegrityAndDataConsistency_Success() throws JsonProcessingException {
        JsonNode mockJsonNode = Mockito.mock(JsonNode.class);
        when(objectMapper.readTree(anyString())).thenReturn(mockJsonNode);
        when(objectMapper.writeValueAsString(mockJsonNode)).thenReturn(retrievedBrokerEntity);
        when(auditRecordService.findLatestConsumerPublishedAuditRecordByEntityId(anyString(), anyString())).thenReturn(Mono.just(auditRecord));

        StepVerifier.create(dataSyncService.verifyRetrievedEntityData("processId", notification, retrievedBrokerEntity))
                .assertNext(retrievedBrokerEntity -> {
                })
                .verifyComplete();

    }

    @Test
    void testVerifyDataIntegrityAndDataConsistency_Failure() throws JsonProcessingException {
        JsonNode mockJsonNode = Mockito.mock(JsonNode.class);
        when(objectMapper.readTree(anyString())).thenReturn(mockJsonNode);
        when(objectMapper.writeValueAsString(mockJsonNode)).thenReturn(retrievedBrokerEntity);
        when(auditRecordService.findLatestConsumerPublishedAuditRecordByEntityId(anyString(), anyString())).thenReturn(Mono.just(errorAuditRecord));

        StepVerifier.create(dataSyncService.verifyRetrievedEntityData("processId", notification, retrievedBrokerEntity))
                .expectError(HashLinkException.class)
                .verify();

    }

    @Test
    void testVerifyDataIntegrity_Failure_HashMismatch() throws JsonProcessingException {
        JsonNode mockJsonNode = Mockito.mock(JsonNode.class);
        when(objectMapper.readTree(anyString())).thenReturn(mockJsonNode);
        when(objectMapper.writeValueAsString(mockJsonNode)).thenReturn(retrievedBrokerEntity);
        when(auditRecordService.findLatestConsumerPublishedAuditRecordByEntityId(anyString(), anyString())).thenReturn(Mono.empty());


        StepVerifier.create(dataSyncService.verifyRetrievedEntityData("processId", errorNotification, retrievedBrokerEntity))
                .expectError(HashLinkException.class)
                .verify();
    }

    @Test
    void testVerifyDataIntegrity_Failure_JsonProcessingExceptionError() throws JsonProcessingException {
        JsonNode mockJsonNode = Mockito.mock(JsonNode.class);
        when(objectMapper.readTree(anyString())).thenReturn(mockJsonNode);
        when(objectMapper.writeValueAsString(mockJsonNode)).thenThrow(JsonProcessingException.class);
        when(auditRecordService.findLatestConsumerPublishedAuditRecordByEntityId(anyString(), anyString())).thenReturn(Mono.empty());


        StepVerifier.create(dataSyncService.verifyRetrievedEntityData("processId", errorNotification, retrievedBrokerEntity))
                .expectError(HashLinkException.class)
                .verify();
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void testVerifySortAttributesAlphabetically(String input, String expected) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, JsonProcessingException {
        //Arrange
        DataSyncServiceImpl dataSyncService = new DataSyncServiceImpl(apiConfig, objectMapper, auditRecordService);
        Method method = DataSyncServiceImpl.class.getDeclaredMethod("sortAttributesAlphabetically", String.class);
        method.setAccessible(true);
        //Act
        String result = (String) method.invoke(dataSyncService, input);
        JsonNode expectedNode = objectMapper.readTree(expected);
        JsonNode resultNode = objectMapper.readTree(result);
        //Assert
        assertEquals(expectedNode, resultNode);
    }
}