package es.in2.desmos.application.runners;

import es.in2.desmos.application.workflows.DataSyncWorkflow;
import es.in2.desmos.application.workflows.PublishWorkflow;
import es.in2.desmos.application.workflows.SubscribeWorkflow;
import es.in2.desmos.domain.models.BlockchainSubscription;
import es.in2.desmos.domain.services.api.BrokerSubscriptionValidateService;
import es.in2.desmos.domain.services.blockchain.BlockchainListenerService;
import es.in2.desmos.domain.services.broker.BrokerListenerService;
import es.in2.desmos.domain.services.sync.services.ExternalYamlService;
import es.in2.desmos.infrastructure.configs.ApiConfig;
import es.in2.desmos.infrastructure.configs.BlockchainConfig;
import es.in2.desmos.infrastructure.configs.BrokerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
    private SubscribeWorkflow subscribeWorkflow;

    @Mock
    private BrokerListenerService brokerListenerService;

    @Mock
    private BlockchainListenerService blockchainListenerService;

    @Mock
    private ExternalYamlService externalYamlService;

    @Mock
    private BrokerSubscriptionValidateService brokerSubscriptionValidateService;

    @InjectMocks
    private ApplicationRunner applicationRunner;

    @Test
    void whenApplicationIsReady_thenSetBrokerSubscriptionAndBlockchainSubscription() {
        // Arrange
        when(brokerListenerService.createSubscription(anyString(), any())).thenReturn(Mono.empty());
        when(blockchainListenerService.createSubscription(anyString(), any(BlockchainSubscription.class))).thenReturn(Mono.empty());
        when(externalYamlService.getAccessNodeYamlDataFromExternalSource(anyString())).thenReturn(Mono.empty());
        when(dataSyncWorkflow.startDataSyncWorkflow(anyString())).thenReturn(Flux.empty());
        when(publishWorkflow.startPublishWorkflow(anyString())).thenReturn(Flux.empty());
        when(subscribeWorkflow.startSubscribeWorkflow(anyString())).thenReturn(Flux.empty());
        when(apiConfig.getCurrentEnvironment()).thenReturn("dev");
        when(brokerSubscriptionValidateService.setSubscriptionId(anyString(), anyString())).thenReturn(Mono.empty());
        // Act
        mock(ApplicationReadyEvent.class);
        //Assert
        StepVerifier.create(applicationRunner.onApplicationReady()).verifyComplete();
    }

    @Test
    void whenDisposeIsActive() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        String getCurrentEnvironment = "dev";
        ApplicationRunner applicationRunner = new ApplicationRunner(apiConfig, brokerConfig, blockchainConfig, brokerListenerService, blockchainListenerService,  externalYamlService, dataSyncWorkflow, publishWorkflow, subscribeWorkflow, brokerSubscriptionValidateService, getCurrentEnvironment);
        Method disposeIfActive = ApplicationRunner.class.getDeclaredMethod("disposeIfActive", Disposable.class);
        disposeIfActive.setAccessible(true);
        Disposable disposable = mock(Disposable.class);
        disposeIfActive.invoke(applicationRunner, disposable);
        when(brokerListenerService.createSubscription(anyString(), any())).thenReturn(Mono.empty());
        when(blockchainListenerService.createSubscription(anyString(), any(BlockchainSubscription.class))).thenReturn(Mono.empty());
        when(externalYamlService.getAccessNodeYamlDataFromExternalSource(anyString())).thenReturn(Mono.empty());
        when(dataSyncWorkflow.startDataSyncWorkflow(anyString())).thenReturn(Flux.empty());
        when(publishWorkflow.startPublishWorkflow(anyString())).thenReturn(Flux.empty());
        when(subscribeWorkflow.startSubscribeWorkflow(anyString())).thenReturn(Flux.empty());
        when(apiConfig.getCurrentEnvironment()).thenReturn("dev");
        when(brokerSubscriptionValidateService.setSubscriptionId(anyString(), anyString())).thenReturn(Mono.empty());
        // Act
        mock(ApplicationReadyEvent.class);
        //Assert
        StepVerifier.create(applicationRunner.onApplicationReady()).verifyComplete();
    }
}

