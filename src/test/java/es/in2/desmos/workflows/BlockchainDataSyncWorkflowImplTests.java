package es.in2.desmos.workflows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.AuditRecordTrader;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.api.BrokerEntityRetrievalService;
import es.in2.desmos.domain.services.api.BrokerEntityVerifyService;
import es.in2.desmos.domain.services.blockchain.adapter.BlockchainAdapterService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.workflows.impl.BlockchainDataSyncWorkflowImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockchainDataSyncWorkflowImplTests {

    @Mock
    private AuditRecordService auditRecordService;
    @Mock
    private BlockchainAdapterService blockchainAdapterService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private BrokerEntityRetrievalService brokerEntityRetrievalService;
    @Mock
    private BrokerEntityVerifyService brokerEntityVerifyService;
    @Mock
    private BrokerPublisherService brokerPublisherService;

    @InjectMocks
    private BlockchainDataSyncWorkflowImpl workflow;

    String brokerEntity = """
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

    String blockchainNotificationJson = """
                {
                    "id": 2240,
                    "publisherAddress": "0x40b0ab9dfd960064fb7e9fdf77f889c71569e349055ff563e8d699d8fa97fa90",
                    "eventType": "ProductOffering",
                    "timestamp": 1712753824,
                    "dataLocation": "http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:ProductOffering:122355255?hl=abbc168236d38354add74d65698f37941947127290cd40a90b4dbe7eb68d25c0",
                    "relevantMetadata": [],
                    "entityId": "0x4eb401aa1248b6a95c298d0747eb470b6ba6fc3f54ea630dc6c77f23ad1abe3e",
                    "previousEntityHash": "0xabbc168236d38354add74d65698f37941947127290cd40a90b4dbe7eb68d25c0"
                }""";

    @Test
    void testStartBlockchainDataSyncWorkflow_withEmptyAuditRecords() {
        String processId = "process123";
        when(auditRecordService.findLatestConsumerPublishedAuditRecord(processId)).thenReturn(Flux.empty());
        when(blockchainAdapterService.getEventsFromRange(eq("process123"), anyLong(), anyLong())).thenReturn(Flux.empty());

        StepVerifier.create(workflow.startBlockchainDataSyncWorkflow(processId))
                .expectSubscription()
                .then(() -> verify(auditRecordService).findLatestConsumerPublishedAuditRecord(processId))
                // Add more expectations as needed
                .verifyComplete();
    }

    @Test
    void testStartBlockchainDataSyncWorkflow_withAuditRecords() throws JsonProcessingException {
        String processId = "process123";
        AuditRecord record = AuditRecord.builder()
                .id(UUID.randomUUID())
                .processId("14f121af-d720-4a53-bc08-fc00bdbbbebe")
                .createdAt(new Timestamp(1713266146360L))
                .entityId("urn:ngsi-ld:ProductOffering:6e00d349-4c49-4bbe-83a9-65115f144908")
                .entityType("ProductOffering")
                .entityHash("a60394397a82adadb646b4cf20c1caa3a2209cbe68e0a898fc3d6cd2008cb2fa") // 9862
                .entityHashLink("56ba5b3c6f0cb990346dd5bc37f4752229c7e712abc8a0ddd16db5eeba711645") // 5284+9862
                .dataLocation("https://domain.org/ngsi-ld/v1/entities/" +
                        "urn:ngsi-ld:ProductOffering:8574a163-6a3d-4fa6-94cc-17e877ec0230" +
                        "?hl=56ba5b3c6f0cb990346dd5bc37f4752229c7e712abc8a0ddd16db5eeba711645")
                .status(AuditRecordStatus.PUBLISHED)
                .trader(AuditRecordTrader.CONSUMER)
                .hash("")
                .hashLink("")
                .newTransaction(true)
                .build(); // Setup this with appropriate data
        when(auditRecordService.findLatestConsumerPublishedAuditRecord(processId)).thenReturn(Flux.just(record));
        when(blockchainAdapterService.getEventsFromRange(eq("process123"), anyLong(), anyLong())).thenReturn(Flux.just(blockchainNotificationJson));
        BlockchainNotification blockchainNotification = BlockchainNotification.builder()
                .id(2240)
                .publisherAddress("0x40b0ab9dfd960064fb7e9fdf77f889c71569e349055ff563e8d699d8fa97fa90")
                .eventType("ProductOffering")
                .timestamp(1712753824)
                .dataLocation("http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:ProductOffering:122355255?hl=abbc168236d38354add74d65698f37941947127290cd40a90b4dbe7eb68d25c0")
                .relevantMetadata(List.of())
                .entityId("0x4eb401aa1248b6a95c298d0747eb470b6ba6fc3f54ea630dc6c77f23ad1abe3e")
                .previousEntityHash("0xabbc168236d38354add74d65698f37941947127290cd40a90b4dbe7eb68d25c0")
                .build();

        when(objectMapper.readValue(eq(blockchainNotificationJson), any(TypeReference.class))).thenReturn(Collections.singletonList(blockchainNotification));
        when(brokerEntityRetrievalService.retrieveEntityFromExternalBroker(processId, blockchainNotification)).thenReturn(Mono.just(brokerEntity));
        when(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(eq("process123"), any(BlockchainNotification.class), any(), eq(AuditRecordStatus.RECEIVED))).thenReturn(Mono.empty());
        when(brokerEntityVerifyService.verifyRetrievedEntityDataIntegrity(eq("process123"), any(BlockchainNotification.class), eq(brokerEntity))).thenReturn(Mono.just(brokerEntity));
        when(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(eq("process123"), any(BlockchainNotification.class), eq(brokerEntity), eq(AuditRecordStatus.RETRIEVED))).thenReturn(Mono.empty());
        when(brokerPublisherService.publishDataToBroker(eq("process123"), any(BlockchainNotification.class), eq(brokerEntity))).thenReturn(Mono.empty());
        when(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(eq("process123"), any(BlockchainNotification.class), eq(brokerEntity), eq(AuditRecordStatus.PUBLISHED))).thenReturn(Mono.empty());

        // Mock further dependencies as necessary

        StepVerifier.create(workflow.startBlockchainDataSyncWorkflow(processId))
                .expectSubscription()
                .then(() -> verify(auditRecordService).findLatestConsumerPublishedAuditRecord(processId))
                .verifyComplete();
    }
}
