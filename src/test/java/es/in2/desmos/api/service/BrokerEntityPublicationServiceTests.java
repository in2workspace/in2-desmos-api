package es.in2.desmos.api.service;

import es.in2.desmos.api.model.BlockchainNotification;
import es.in2.desmos.api.service.impl.BrokerEntityPublicationServiceImpl;
import es.in2.desmos.api.util.ApplicationUtils;
import es.in2.desmos.broker.service.BrokerPublicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrokerEntityPublicationServiceTests {

    @Mock
    private TransactionService transactionService;
    @Mock
    private BrokerPublicationService brokerPublicationService;
    @InjectMocks
    private BrokerEntityPublicationServiceImpl brokerEntityPublicationService;

    @Test
    void testDeletedEntityNotification() {
        // Arrange
        String processId = "process123";
        String entityId = "entity123";
        BlockchainNotification notification = BlockchainNotification.builder()
                .dataLocation("http://broker.internal/entities/entity123")
                .build();
        when(brokerPublicationService.deleteEntityById(processId, entityId)).thenReturn(Mono.empty());
        when(transactionService.saveTransaction(any(), any())).thenReturn(Mono.empty());
        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
            applicationUtils
                    .when(() -> ApplicationUtils.extractEntityIdFromDataLocation(anyString()))
                    .thenReturn(entityId);
            // Act & Assert
            StepVerifier.create(
                            brokerEntityPublicationService.publishRetrievedEntityToBroker(processId, "{errorCode: 404}", notification))
                    .verifyComplete();
        }
    }

}