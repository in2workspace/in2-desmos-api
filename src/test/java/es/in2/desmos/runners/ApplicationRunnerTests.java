package es.in2.desmos.runners;

import es.in2.desmos.configs.BlockchainConfig;
import es.in2.desmos.configs.BrokerConfig;
import es.in2.desmos.domain.models.BlockchainSubscription;
import es.in2.desmos.domain.services.blockchain.BlockchainListenerService;
import es.in2.desmos.domain.services.broker.BrokerListenerService;
import es.in2.desmos.workflows.DataSyncWorkflow;
import es.in2.desmos.workflows.PublishWorkflow;
import es.in2.desmos.workflows.SubscribeWorkflow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationRunnerTests {

    @Mock
    private BrokerConfig brokerConfig;

    @Mock
    private BlockchainConfig blockchainConfig;

    @Mock
    private DataSyncWorkflow dataSyncWorkflow;

    @Mock
    private PublishWorkflow publishWorkflow;

    @Mock
    private SubscribeWorkflow subscribeWorkflow;

    @Mock
    private BrokerListenerService brokerListenerService;

    @Mock
    private BlockchainListenerService blockchainListenerService;

    @InjectMocks
    private ApplicationRunner applicationRunner;

    @Test
    void whenApplicationIsReady_thenSetBrokerSubscriptionAndBlockchainSubscription() {
        // Arrange
        when(brokerListenerService.createSubscription(anyString(), any())).thenReturn(Mono.empty());
        Mockito.when(blockchainListenerService.createSubscription(Mockito.anyString(), Mockito.any(BlockchainSubscription.class)))
                .thenReturn(Mono.empty());
        Mockito.when(dataSyncWorkflow.startDataSyncWorkflow(Mockito.anyString())).thenReturn(Flux.empty());
        Mockito.when(publishWorkflow.startPublishWorkflow(Mockito.anyString())).thenReturn(Flux.empty());
        Mockito.when(subscribeWorkflow.startSubscribeWorkflow()).thenReturn(Flux.empty());
        // Act
        ApplicationReadyEvent event = mock(ApplicationReadyEvent.class);
        StepVerifier.create(applicationRunner.onApplicationReady()).verifyComplete();
    }

    // Fixme: Retry and recover does not work with MockitoExtension
//    @Test
//    void whenBrokerSubscriptionFails_thenRetry() {
//        // Arrange
//        when(brokerListenerService.createSubscription(anyString(), any()))
//                .thenThrow(new RequestErrorException("Simulated failure"))
//                .thenReturn(Mono.empty()); // Success on a second attempt
//        // Act
//        ApplicationReadyEvent event = mock(ApplicationReadyEvent.class);
//        initialSubscriptionRunner.setBrokerSubscription().block();
//        // Assert
//        verify(brokerListenerService, times(2)).createSubscription(anyString(), any());
//    }

    // Fixme: Retry and recover does not work with MockitoExtension
//    @Test
//    void whenBrokerSubscriptionContinuesToFail_thenRecover() {
//        // Arrange
//        when(brokerListenerService.createSubscription(anyString(), any()))
//                .thenThrow(new RequestErrorException("Simulated failure"));
//        // Act
//        ApplicationReadyEvent event = mock(ApplicationReadyEvent.class);
//        try {
//            initialSubscriptionRunner.setBrokerSubscription().block();
//        } catch (Exception e) {
//            // Expected exception
//        }
//        // Assert
//        verify(brokerListenerService, times(4)).createSubscription(anyString(), any());
//        // Note: In reality, you'd want to capture and assert the invocation of the recover method
//        // This may require a refactor to your runner to make it more testable in this scenario.
//    }

}
