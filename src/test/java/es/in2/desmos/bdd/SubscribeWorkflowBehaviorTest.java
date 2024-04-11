package es.in2.desmos.bdd;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import es.in2.desmos.ContainerManager;
import es.in2.desmos.controllers.NotificationController;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.repositories.AuditRecordRepository;
import es.in2.desmos.domain.services.api.QueueService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SubscribeWorkflowBehaviorTest {

    private final Logger log = LoggerFactory.getLogger(PublishWorkflowBehaviorTest.class);

    private final ObjectMapper objectMapper =
            JsonMapper.builder()
                    .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                    .build();

    @Autowired
    private NotificationController notificationController;

    @Autowired
    private AuditRecordRepository auditRecordRepository;

    @Autowired
    private QueueService pendingSubscribeEventsQueue;

    @Autowired
    private BrokerPublisherService brokerPublisherService;

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    @AfterEach
    public void cleanUp() {
        auditRecordRepository.deleteAll().block();
    }

    @Order(0)
    void postEntityToScorpio() {
        log.info("1. Send a POST request to the external broker in order to retrieve the entity later");
        //todo: use webclient to send a POST request to the external broker
    }
    @Order(1)
    @Test
    void subscribeWorkflowBehaviorTest() {
        log.info("Starting Subscribe Workflow Behavior Test...");
        /*
            Given a BlockchainNotification, we will send a POST request emulating the broker behavior.
            When the POST request is received, the application will retrieve a BrokerEntity from the external broker,
            and then it will send a POST request to the local broker.
            Finally, the application will create an AuditRecord with the status of the operation.
        */
        String blockchainNotificationJson = """
                {
                    "id": 2240,
                    "publisherAddress": "0x40b0ab9dfd960064fb7e9fdf77f889c71569e349055ff563e8d699d8fa97fa90",
                    "eventType": "ProductOffering",
                    "timestamp": 1712753824,
                    "dataLocation": "http://scorpio:9090/ngsi-ld/v1/entities/urn:ngsi-ld:ProductOffering:122355255?hl=d6a66d3d0ee1cdd0072b0ee10a86512d63a6bb62efb2cd796f63b5dc4f35c48c",
                    "relevantMetadata": [],
                    "entityId": "0x4eb401aa1248b6a95c298d0747eb470b6ba6fc3f54ea630dc6c77f23ad1abe3e",
                    "previousEntityHash": "0xab449ff6b5e3c1331ceaa48168e6d8d33956f3ae5f3f3423d905f5345914e474"
                }""";



        // When
        try {
            log.info("1. Create a BlockchainNotification and send a POST request to the application");
            BlockchainNotification blockchainNotification = objectMapper.readValue(blockchainNotificationJson, BlockchainNotification.class);
            notificationController.postDLTNotification(blockchainNotification).block();
            log.info("1.1. Get the event stream from the pendingSubscribeQueue and subscribe to it.");
            pendingSubscribeEventsQueue.getEventStream().subscribe(event -> log.info("Event: {}", event));
            //todo: when the event is received, the application will retrieve the entity from the external broker, check the audit record and publish the entity to the local broker

        } catch (Exception e) {
            log.error("Error while sending the BlockchainNotification: {}", e.getMessage());
        }
        }


    }
