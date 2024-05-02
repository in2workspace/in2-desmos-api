package es.in2.desmos.application.runners;

import es.in2.desmos.application.workflows.DataSyncWorkflow;
import es.in2.desmos.application.workflows.PublishWorkflow;
import es.in2.desmos.application.workflows.SubscribeWorkflow;
import es.in2.desmos.domain.models.BlockchainSubscription;
import es.in2.desmos.domain.services.blockchain.BlockchainListenerService;
import es.in2.desmos.domain.services.broker.BrokerListenerService;
import es.in2.desmos.domain.services.sync.jobs.BlockchainDataSyncJob;
import es.in2.desmos.infrastructure.configs.ApiConfig;
import es.in2.desmos.infrastructure.configs.BlockchainConfig;
import es.in2.desmos.infrastructure.configs.BrokerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationRunnerTests {

    @Mock
    private ApiConfig apiConfig;

    @Mock
    private BrokerConfig brokerConfig;

    @Mock
    private BlockchainConfig blockchainConfig;

    @Mock
    private DataSyncWorkflow dataSyncWorkflow;

    @Mock
    private PublishWorkflow publishWorkflow;

    @Mock
    private BlockchainDataSyncJob blockchainDataSyncJob;

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
        when(blockchainListenerService.createSubscription(anyString(), any(BlockchainSubscription.class))).thenReturn(Mono.empty());
        when(dataSyncWorkflow.startDataSyncWorkflow(anyString())).thenReturn(Flux.empty());
        when(publishWorkflow.startPublishWorkflow(anyString())).thenReturn(Flux.empty());
        when(subscribeWorkflow.startSubscribeWorkflow(anyString())).thenReturn(Flux.empty());
        when(apiConfig.getCurrentEnvironment()).thenReturn("dev");
        // Act
        mock(ApplicationReadyEvent.class);
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
