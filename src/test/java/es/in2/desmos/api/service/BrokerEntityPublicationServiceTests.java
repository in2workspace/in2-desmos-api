package es.in2.desmos.api.service;//package es.in2.connector.api.service;
//
//import es.in2.connector.api.model.BlockchainNotification;
//import es.in2.connector.api.service.impl.BrokerEntityPublicationServiceImpl;
//import es.in2.connector.broker.service.BrokerPublicationService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class BrokerEntityPublicationServiceTests {
//
//    @Mock
//    private TransactionService transactionService;
//    @Mock
//    private BrokerPublicationService brokerPublicationService;
//    @Mock
//    private BrokerEntityPublicationServiceImpl brokerEntityPublicationService;
//
//    @Test
//    void testDeletedEntityNotification() {
//        String processId = "process123";
//        String entityId = "entity123";
//        BlockchainNotification notification = BlockchainNotification.builder()
//                .dataLocation("http://broker.internal/entities/entity123")
//                .build();
//
//        when(brokerPublicationService.deleteEntityById(processId, entityId)).thenReturn(Mono.empty());
//        when(transactionService.saveTransaction(any(), any())).thenReturn(Mono.empty());
//
//        StepVerifier.create(brokerEntityPublicationService.publishRetrievedEntityToBroker(processId, "{errorCode: 404}", notification))
//                .verifyComplete();
//    }
//
//}
