//package es.in2.desmos.application.todo;
//
//import es.in2.desmos.application.todo.BlockchainToBrokerDataSyncSynchronizer;
//import es.in2.desmos.domain.model.DLTNotification;
//import es.in2.desmos.domain.service.BrokerEntityPublisherService;
//import es.in2.desmos.domain.service.BrokerEntityRetrievalService;
//import es.in2.desmos.application.service.NotificationProcessorService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class BlockchainToBrokerDataSyncSynchronizerImpl implements BlockchainToBrokerDataSyncSynchronizer {
//
//    private final NotificationProcessorService notificationProcessorService;
//    private final BrokerEntityRetrievalService brokerEntityRetrievalService;
//    private final BrokerEntityPublisherService brokerEntityPublisherService;
//
//    @Override
//    public Mono<Void> retrieveAndSynchronizeEntityIntoBroker(String processId, DLTNotification dltNotification) {
//        return notificationProcessorService.processDLTNotification(processId, dltNotification)
//                // Try to retrieve the Entity from the source Broker
//                .then(brokerEntityRetrievalService.retrieveEntityFromSourceBroker(processId, dltNotification))
//                // Publish the retrieved Entity to own Broker
//                .flatMap(retrievedEntity -> brokerEntityPublisherService
//                        .publishRetrievedEntityToBroker(processId, retrievedEntity, dltNotification))
//                .doOnSuccess(voidValue -> log.info("ProcessID: {} - Entity retrieval, validation, and publication completed", processId))
//                .doOnError(e -> log.error("ProcessID: {} - Error retrieving, validating, and publishing entity", processId, e));
//    }
//
//}
