package es.in2.desmos.domain.services.broker;

import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.services.broker.adapter.BrokerAdapterService;
import es.in2.desmos.domain.services.broker.adapter.factory.BrokerAdapterFactory;
import es.in2.desmos.domain.services.broker.impl.BrokerPublisherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrokerPublisherServiceTests {

    private final long id = 1234;
    private final String publisherAddress = "http://blockchain-testnode.infra.svc.cluster.local:8545/";
    private final String eventType = "ProductOffering";
    private final long timestamp = 1711801566;
    private final String dataLocation = "http://localhost:8080/ngsi-ld/v1/entities/" +
            "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69" +
            "?hl=03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
    private final List<String> relevantMetadata = List.of("metadata1", "metadata2");
    private final String entityIdHash = "6f6468ded8276d009ab1b6c578c2b922053acd6b5a507f36d408d3f7c9ae91d0";
    private final String previousEntityHash = "98d9658d98764dbe135b316f52a98116b4b02f9d7e57212aa86335c42a58539a";

    BlockchainNotification blockchainNotification = BlockchainNotification.builder()
            .id(id)
            .publisherAddress(publisherAddress)
            .eventType(eventType)
            .timestamp(timestamp)
            .dataLocation(dataLocation)
            .relevantMetadata(relevantMetadata)
            .entityId(entityIdHash)
            .previousEntityHash(previousEntityHash)
            .build();

    @Mock
    private BrokerAdapterFactory brokerAdapterFactory;

    @Mock
    private BrokerAdapterService brokerAdapterService;

    private BrokerPublisherServiceImpl brokerPublisherService;

    @BeforeEach
    void init() {
        when(brokerAdapterFactory.getBrokerAdapter()).thenReturn(brokerAdapterService);
        brokerPublisherService = new BrokerPublisherServiceImpl(brokerAdapterFactory);
    }

    @Test
    void testPublishDataToBroker() {
        //Arrange
        String processId = "processId";
        String retrievedBrokerEntity = "retrievedBrokerEntity";
        //Act
        when(brokerAdapterService.getEntityById(eq(processId), anyString())).thenReturn(Mono.just(""));
        when(brokerAdapterService.postEntity(processId, retrievedBrokerEntity)).thenReturn(Mono.empty());
        //Assert
        StepVerifier.create(brokerPublisherService.publishDataToBroker(processId, blockchainNotification, retrievedBrokerEntity))
                .verifyComplete();
    }

    @Test
    void testPublishDataToBrokerWithUpdate() {
        //Arrange
        String processId = "processId";
        String retrievedBrokerEntity = "retrievedBrokerEntity";
        //Act
        when(brokerAdapterService.getEntityById(eq(processId), anyString())).thenReturn(Mono.just("entityId"));
        when(brokerAdapterService.updateEntity(processId, retrievedBrokerEntity)).thenReturn(Mono.empty());
        //Assert
        StepVerifier.create(brokerPublisherService.publishDataToBroker(processId, blockchainNotification, retrievedBrokerEntity))
                .verifyComplete();
    }
}