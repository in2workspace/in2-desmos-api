package es.in2.desmos.runners;

import es.in2.desmos.configs.BlockchainConfig;
import es.in2.desmos.configs.BrokerConfig;
import es.in2.desmos.domain.models.BlockchainSubscription;
import es.in2.desmos.domain.services.blockchain.BlockchainListenerService;
import es.in2.desmos.domain.services.broker.BrokerListenerService;
import es.in2.desmos.workflows.DataSyncWorkflow;
import es.in2.desmos.workflows.PublishWorkflow;
import es.in2.desmos.workflows.SubscribeWorkflow;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
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

import java.util.List;

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
        List<String> expectedEntityTypes = List.of("ProductOffering", "Catalogue", "Category");
        when(brokerConfig.getNotificationEndpoint()).thenReturn("http://example.com/notifications");
        when(blockchainConfig.getEntityTypes()).thenReturn(expectedEntityTypes);
        when(blockchainConfig.getNotificationEndpoint()).thenReturn("http://example.com/notifications");
        when(brokerListenerService.createSubscription(anyString(), any())).thenReturn(Mono.empty());
        when(blockchainListenerService.createSubscription(anyString(), Mockito.any(BlockchainSubscription.class)))
                .thenReturn(Mono.empty());
        when(dataSyncWorkflow.startDataSyncWorkflow(Mockito.anyString())).thenReturn(Flux.empty());
        when(publishWorkflow.startPublishWorkflow(Mockito.anyString())).thenReturn(Flux.empty());
        when(subscribeWorkflow.startSubscribeWorkflow()).thenReturn(Flux.empty());
        // Act
        ApplicationReadyEvent event = mock(ApplicationReadyEvent.class);
        StepVerifier.create(applicationRunner.onApplicationReady()).verifyComplete();
    }

    @Test
    void whenBlockchainSubscriptionIsInvalid_thenErrorIsThrown() {
        // Arrange
        List<String> invalidEntityTypes = List.of("   ", "ProductOffering", "");
        lenient().when(blockchainConfig.getEntityTypes()).thenReturn(invalidEntityTypes);
        lenient().when(blockchainConfig.getNotificationEndpoint()).thenReturn("");
        lenient().when(brokerListenerService.createSubscription(anyString(), any())).thenReturn(Mono.empty());
        lenient().when(blockchainListenerService.createSubscription(anyString(), Mockito.any(BlockchainSubscription.class)))
                .thenReturn(Mono.empty());
        lenient().when(dataSyncWorkflow.startDataSyncWorkflow(Mockito.anyString())).thenReturn(Flux.empty());
        lenient().when(publishWorkflow.startPublishWorkflow(Mockito.anyString())).thenReturn(Flux.empty());
        lenient().when(subscribeWorkflow.startSubscribeWorkflow()).thenReturn(Flux.empty());
        // Act & Assert
        StepVerifier.create(applicationRunner.onApplicationReady())
                .expectErrorMatches(throwable -> {
                    if (throwable instanceof ConstraintViolationException cve) {
                        for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                            System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
                        }
                        return true;
                    }
                    return false;
                })
                .verify();
    }

    @Test
    void whenBrokerSubscriptionIsInvalid_thenErrorIsThrown() {
        // Arrange
        lenient().when(blockchainConfig.getEntityTypes()).thenReturn(List.of("   ", "ProductOffering", ""));
        lenient().when(blockchainConfig.getNotificationEndpoint()).thenReturn("http://example.com/notifications");
        lenient().when(brokerListenerService.createSubscription(anyString(), any())).thenReturn(Mono.empty());
        lenient().when(blockchainListenerService.createSubscription(anyString(), Mockito.any(BlockchainSubscription.class)))
                .thenReturn(Mono.empty());
        lenient().when(dataSyncWorkflow.startDataSyncWorkflow(Mockito.anyString())).thenReturn(Flux.empty());
        lenient().when(publishWorkflow.startPublishWorkflow(Mockito.anyString())).thenReturn(Flux.empty());
        lenient().when(subscribeWorkflow.startSubscribeWorkflow()).thenReturn(Flux.empty());
        // Act & Assert
        StepVerifier.create(applicationRunner.onApplicationReady())
                .expectErrorMatches(throwable -> {
                    if (throwable instanceof ConstraintViolationException cve) {
                        for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                            System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
                        }
                        return true;
                    }
                    return false;
                })
                .verify();
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