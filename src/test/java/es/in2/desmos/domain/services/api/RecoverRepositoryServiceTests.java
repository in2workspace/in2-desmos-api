package es.in2.desmos.domain.services.api;

import es.in2.desmos.domain.models.BlockchainNotificationRecover;
import es.in2.desmos.domain.models.BlockchainTxPayloadRecover;
import es.in2.desmos.domain.repositories.PublishWorkflowRecoverRepository;
import es.in2.desmos.domain.repositories.SubscribeWorkflowRecoverRepository;
import es.in2.desmos.domain.services.api.impl.RecoverRepositoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecoverRepositoryServiceTests {

    @Mock
    private PublishWorkflowRecoverRepository publishWorkflowRecoverRepository;
    @Mock
    private SubscribeWorkflowRecoverRepository subscribeWorkflowRecoverRepository;

    private RecoverRepositoryServiceImpl recoverRepositoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recoverRepositoryService = new RecoverRepositoryServiceImpl(publishWorkflowRecoverRepository, subscribeWorkflowRecoverRepository);
    }

    @Test
    void saveBlockchainTxPayloadRecover_whenSaveSuccess() {
        BlockchainTxPayloadRecover txPayloadRecover = new BlockchainTxPayloadRecover();
        when(publishWorkflowRecoverRepository.save(txPayloadRecover)).thenReturn(Mono.empty());

        Mono<Void> result = recoverRepositoryService.saveBlockchainTxPayloadRecover("test-process-id", txPayloadRecover);

        StepVerifier.create(result)
                .verifyComplete();

        verify(publishWorkflowRecoverRepository).save(txPayloadRecover);
    }

    @Test
    void saveBlockchainTxPayloadRecover_whenSaveFails() {
        BlockchainTxPayloadRecover txPayloadRecover = new BlockchainTxPayloadRecover();
        when(publishWorkflowRecoverRepository.save(txPayloadRecover)).thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<Void> result = recoverRepositoryService.saveBlockchainTxPayloadRecover("test-process-id", txPayloadRecover);

        StepVerifier.create(result)
                .verifyErrorMatches(th -> th instanceof RuntimeException && th.getMessage().equals("Database error"));

        verify(publishWorkflowRecoverRepository).save(txPayloadRecover);
    }

    @Test
    void saveBlockchainNotificationRecover_whenSaveSuccess() {
        BlockchainNotificationRecover notificationRecover = new BlockchainNotificationRecover();
        when(subscribeWorkflowRecoverRepository.save(notificationRecover)).thenReturn(Mono.empty());

        Mono<Void> result = recoverRepositoryService.saveBlockchainNotificationRecover("test-process-id", notificationRecover);

        StepVerifier.create(result)
                .verifyComplete();

        verify(subscribeWorkflowRecoverRepository).save(notificationRecover);
    }

    @Test
    void saveBlockchainNotificationRecover_whenSaveFails() {
        BlockchainNotificationRecover notificationRecover = new BlockchainNotificationRecover();
        when(subscribeWorkflowRecoverRepository.save(notificationRecover)).thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<Void> result = recoverRepositoryService.saveBlockchainNotificationRecover("test-process-id", notificationRecover);

        StepVerifier.create(result)
                .verifyErrorMatches(th -> th instanceof RuntimeException && th.getMessage().equals("Database error"));

        verify(subscribeWorkflowRecoverRepository).save(notificationRecover);
    }
}
