//package es.in2.desmos.application.todo;
//
//import es.in2.desmos.application.todo.BrokerToBlockchainDataSyncPublisher;
//import es.in2.desmos.domain.service.DLTEventCreatorService;
//import es.in2.desmos.domain.service.BrokerEntityProcessorService;
//import es.in2.desmos.infrastructure.blockchain.service.DLTAdapterPublisher;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//import java.util.Objects;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class BrokerToBlockchainDataSyncPublisherImpl implements BrokerToBlockchainDataSyncPublisher {
//
//    private final BrokerEntityProcessorService brokerEntityProcessorService;
//    private final DLTEventCreatorService dltEventCreatorService;
//    private final DLTAdapterPublisher dltAdapterPublisher;
//
//    @Override
//    public Mono<Void> createAndSynchronizeBlockchainEvents(String processId, String brokerEntityId) {
//        log.debug("Creating and synchronizing Blockchain Events for Broker Entity with id: {}", brokerEntityId);
//        return brokerEntityProcessorService.processBrokerEntity(processId, brokerEntityId)
//                .filter(Objects::nonNull)
//                // Create a Blockchain Event -> BlockchainEventCreator
//                .flatMap(dataMap -> dltEventCreatorService.createBlockchainEvent(processId, dataMap))
//                // Publish the Blockchain Event into the Blockchain Node -> BlockchainEventPublisher
//                .flatMap(blockchainEvent -> dltAdapterPublisher.publishBlockchainEvent(processId, blockchainEvent))
//                .doOnSuccess(success -> log.info("Blockchain Event creation and publication completed."))
//                .doOnError(error -> log.error("Error creating or publishing Blockchain Event"));
//    }
//
//}