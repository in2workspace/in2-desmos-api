//package es.in2.desmos.api.controller;
//
//import es.in2.desmos.api.facade.BlockchainToBrokerSynchronizer;
//import es.in2.desmos.api.facade.BrokerToBlockchainPublisher;
//import es.in2.desmos.api.model.BlockchainNotification;
//import es.in2.desmos.api.model.BrokerNotification;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//import java.util.Map;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class NotificationControllerTest {
//
//    @Mock
//    private BrokerToBlockchainPublisher brokerToBlockchainPublisher;
//
//    @Mock
//    private BlockchainToBrokerSynchronizer blockchainToBrokerSynchronizer;
//
//    @InjectMocks
//    private NotificationController notificationController;
//
//    @Test
//    void testCaptureBrokerNotification() {
//        // Arrange
//        BrokerNotification brokerNotification = BrokerNotification.builder().id("id").type("type").data(List.of(Map.of("key", "value"))).subscriptionId("subscriptionId").notifiedAt("notifiedAt").build();
//        // Mock the behavior of processAndPublishBrokerNotificationToBlockchain in BrokerToBlockchainPublisher
//        when(brokerToBlockchainPublisher.processAndPublishBrokerNotificationToBlockchain(anyString(), any(BrokerNotification.class))).thenReturn(Mono.empty());
//        // Act
//        WebTestClient.bindToController(notificationController).build().post().uri("/notifications/broker").bodyValue(brokerNotification).exchange().expectStatus().isEqualTo(HttpStatus.OK);
//    }
//
//    @Test
//    void testDltNotification() {
//        // Arrange
//        BlockchainNotification blockchainNotification = BlockchainNotification.builder().id(BlockchainNotification.Id.builder().type("type").hex("hex").build()).publisherAddress("publisherAddress").eventType("eventType").timestamp(BlockchainNotification.Timestamp.builder().type("type").hex("hex").build()).dataLocation("dataLocation").relevantMetadata(List.of("metadata1", "metadata2")).build();
//        // Mock the behavior of retrieveAndPublishEntityIntoBroker in BlockchainToBrokerSynchronizer
//        when(blockchainToBrokerSynchronizer.retrieveAndPublishEntityToBroker(anyString(), any(BlockchainNotification.class))).thenReturn(Mono.empty());
//        // Act
//        WebTestClient.bindToController(notificationController).build().post().uri("/notifications/dlt").bodyValue(blockchainNotification).exchange().expectStatus().isEqualTo(HttpStatus.OK);
//    }
//
//}
