package es.in2.desmos.broker.adapter;

import es.in2.desmos.broker.model.BrokerSubscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(MockitoExtension.class)
public class OrionLdAdapterTests {

    private String processId;
    @Mock
    private BrokerSubscription brokerSubscription;
    @InjectMocks
    private OrionLdAdapter orionLdAdapter;

    @BeforeEach
    void setUp() {
        processId = "testProcessId";
    }

    @Test
    void testPostEntity() {
        //Arrange
        String requestBody = "testRequestBody";
        Mono<Void> result = orionLdAdapter.postEntity(processId, requestBody);
        //Assert
        assertNull(result);
    }

    @Test
    void testGetEntitiesByTimeRange() {
        //Arrange
        String timestamp = "testTimestamp";
        Flux<String> result = orionLdAdapter.getEntitiesByTimeRange(processId, timestamp);
        //Assert
        assertNull(result);
    }

    @Test
    void testGetEntityById() {
        //Arrange
        String entityId = "testEntityId";
        Mono<String> result = orionLdAdapter.getEntityById(processId, entityId);
        //Assert
        assertNull(result);
    }

    @Test
    void testUpdateEntity() {
        //Arrange
        String requestBody = "testRequestBody";
        Mono<Void> result = orionLdAdapter.updateEntity(processId, requestBody);
        //Assert
        assertNull(result);
    }

    @Test
    void testDeleteEntityById() {
        //Arrange
        String entityId = "testEntityId";
        Mono<Void> result = orionLdAdapter.deleteEntityById(processId, entityId);
        //Assert
        assertNull(result);
    }

    @Test
    void testCreateSubscription() {
        //Arrange
        Mono<Void> result = orionLdAdapter.createSubscription(processId, brokerSubscription);
        //Assert
        assertNull(result);
    }


    @Test
    void testGetSubscriptions() {
        //Arrange
        Mono<List<BrokerSubscription>> result = orionLdAdapter.getSubscriptions(processId);
        //Assert
        assertNull(result);
    }

    @Test
    void testUpdateSubscription() {
        //Arrange
        Mono<Void> result = orionLdAdapter.updateSubscription(processId, brokerSubscription);
        //Assert
        assertNull(result);
    }

    @Test
    void testDeleteSubscription() {
        //Arrange
        String subscriptionId = "testSubscriptionId";
        Mono<Void> result = orionLdAdapter.deleteSubscription(processId, subscriptionId);
        //Assert
        assertNull(result);
    }
}