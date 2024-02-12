package es.in2.desmos.api.service;

import es.in2.desmos.api.model.BlockchainNotification;
import es.in2.desmos.api.service.impl.BrokerEntityPublisherServiceImpl;
import es.in2.desmos.api.util.ApplicationUtils;
import es.in2.desmos.broker.service.BrokerPublicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.security.NoSuchAlgorithmException;

import static es.in2.desmos.api.util.ApplicationUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrokerEntityPublicationServiceTests {

    String processId;
    String entityId;
    BlockchainNotification notification;
    @Mock
    private TransactionService transactionService;
    @Mock
    private BrokerPublicationService brokerPublicationService;
    @InjectMocks
    private BrokerEntityPublisherServiceImpl brokerEntityPublicationService;

    @BeforeEach
    void setUp() {
        processId = "process123";
        entityId = "entity123";
        notification = BlockchainNotification.builder()
                .dataLocation("http://broker.internal/entities/entity123")
                .build();
    }


    @Test
    void testDeletedEntityNotification() {
        // Arrange
        when(brokerPublicationService.deleteEntityById(processId, entityId)).thenReturn(Mono.empty());
        when(transactionService.saveTransaction(any(), any())).thenReturn(Mono.empty());
        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
            applicationUtils
                    .when(() -> extractEntityIdFromDataLocation(anyString()))
                    .thenReturn(entityId);
            // Act & Assert
            StepVerifier.create(
                            brokerEntityPublicationService.publishRetrievedEntityToBroker(processId, "{errorCode: 404}", notification))
                    .verifyComplete();
        }
    }

    @Test
    void testNotDeletedEntityNotification() {
        // Arrange
        String retrievedBrokerEntity = "brokerEntity";
        when(brokerPublicationService.getEntityById(processId, entityId)).thenReturn(Mono.just("{errorCode: 404}"));
        when(brokerPublicationService.postEntity(processId, retrievedBrokerEntity)).thenReturn(Mono.empty());
        when(transactionService.saveTransaction(any(), any())).thenReturn(Mono.empty());
        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
            applicationUtils
                    .when(() -> extractEntityIdFromDataLocation(anyString()))
                    .thenReturn(entityId);
            applicationUtils
                    .when(() -> calculateSHA256Hash(anyString()))
                    .thenReturn(entityId);
            applicationUtils
                    .when(() -> extractEntityHashFromDataLocation(anyString()))
                    .thenReturn(entityId);
            // Act & Assert
            StepVerifier.create(
                            brokerEntityPublicationService.publishRetrievedEntityToBroker(processId, retrievedBrokerEntity, notification))
                    .verifyComplete();
        }
    }

    @Test
    void testValidEntityIntegrity() {
        // Arrange
        String retrievedBrokerEntity = "brokerEntity";
        when(brokerPublicationService.getEntityById(processId, entityId)).thenReturn(Mono.just("Ok"));
        when(brokerPublicationService.updateEntity(processId, retrievedBrokerEntity)).thenReturn(Mono.empty());
        when(transactionService.saveTransaction(any(), any())).thenReturn(Mono.empty());
        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
            applicationUtils
                    .when(() -> extractEntityIdFromDataLocation(anyString()))
                    .thenReturn(entityId);
            applicationUtils
                    .when(() -> calculateSHA256Hash(anyString()))
                    .thenReturn(entityId);
            applicationUtils
                    .when(() -> extractEntityHashFromDataLocation(anyString()))
                    .thenReturn(entityId);
            // Act & Assert
            StepVerifier.create(
                            brokerEntityPublicationService.publishRetrievedEntityToBroker(processId, retrievedBrokerEntity, notification))
                    .verifyComplete();
        }
    }

    @Test
    void testInvalidEntityIntegrity() {
        // Arrange
        String retrievedBrokerEntity = "brokerEntity";

        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
            applicationUtils
                    .when(() -> extractEntityIdFromDataLocation(anyString()))
                    .thenReturn(entityId);
            applicationUtils
                    .when(() -> calculateSHA256Hash(anyString()))
                    .thenReturn("entity1");
            applicationUtils
                    .when(() -> extractEntityHashFromDataLocation(anyString()))
                    .thenReturn("entity2");
            // Act & Assert
            StepVerifier.create(
                            brokerEntityPublicationService.publishRetrievedEntityToBroker(processId, retrievedBrokerEntity, notification))
                    .verifyError(IllegalArgumentException.class);
        }
    }

    @Test
    void testCatchException() {
        // Arrange
        String retrievedBrokerEntity = "brokerEntity";
        try (MockedStatic<ApplicationUtils> applicationUtils = Mockito.mockStatic(ApplicationUtils.class)) {
            applicationUtils
                    .when(() -> extractEntityIdFromDataLocation(anyString()))
                    .thenReturn(entityId);
            applicationUtils
                    .when(() -> calculateSHA256Hash(anyString()))
                    .thenThrow(NoSuchAlgorithmException.class);
            // Act & Assert
            StepVerifier.create(
                            brokerEntityPublicationService.publishRetrievedEntityToBroker(processId, retrievedBrokerEntity, notification))
                    .verifyError(NoSuchAlgorithmException.class);
        }
    }
}