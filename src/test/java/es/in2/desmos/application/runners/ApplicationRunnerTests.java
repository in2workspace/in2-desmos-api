package es.in2.desmos.application.runners;

import es.in2.desmos.application.workflows.DataSyncWorkflow;
import es.in2.desmos.domain.models.BlockchainSubscription;
import es.in2.desmos.domain.services.api.SubscriptionManagerService;
import es.in2.desmos.domain.services.blockchain.BlockchainListenerService;
import es.in2.desmos.domain.services.broker.BrokerListenerService;
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
import static org.mockito.Mockito.*;

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
    private SubscriptionManagerService subscriptionManagerService;

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
        when(apiConfig.getCurrentEnvironment()).thenReturn("dev");
        // Act
        mock(ApplicationReadyEvent.class);
        //Assert
        StepVerifier.create(applicationRunner.onApplicationReady()).verifyComplete();
    }
}

