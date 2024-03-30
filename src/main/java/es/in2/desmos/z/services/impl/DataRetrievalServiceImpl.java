//package es.in2.desmos.z.services.impl;
//
//import es.in2.desmos.z.services.DataRetrievalService;
//import es.in2.desmos.z.domain.model.DLTNotification;
//import es.in2.desmos.z.domain.service.BrokerEntityPublisherService;
//import es.in2.desmos.z.domain.service.BrokerEntityRetrievalService;
//import es.in2.desmos.z.domain.service.QueueService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.util.Objects;
//import java.util.UUID;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class DataRetrievalServiceImpl implements DataRetrievalService {
//
//    private final BrokerEntityRetrievalService brokerEntityRetrievalService;
//    private final BrokerEntityPublisherService brokerEntityPublisherService;
//    private final QueueService dataRetrievalQueue;
//
//    @Override
//    public Flux<Void> startRetrievingData() {
//        String processId = UUID.randomUUID().toString();
//        log.debug("Process of retrieving data from the external sources started with processId: {}", processId);
//        // The dataRetrievalQueue is a QueueService object used to retrieve data from the external sources
//        return dataRetrievalQueue.getEventStream()
//                // get the next DLTNotification from the queue
//                .flatMap(eventQueue -> Mono.just((DLTNotification) eventQueue.getEvent().get(0))
//                        // verify that the DLTNotification is not null
//                        .filter(Objects::nonNull)
//                        // retrieve the entity from the source broker
//                        .flatMap(dltNotification ->
//                                brokerEntityRetrievalService.retrieveEntityFromSourceBroker(processId, dltNotification)
//                                        // validate and publish the retrieved entity
//                                        .flatMap(retrievedEntity ->
//                                                brokerEntityPublisherService.publishRetrievedEntityToBroker(processId, retrievedEntity, dltNotification))
//                        )
//                        .doOnSuccess(voidValue -> log.info("ProcessID: {} - Entity retrieval, validation, and publication completed", processId))
//                        .doOnError(e -> log.error("ProcessID: {} - Error retrieving, validating, and publishing entity", processId)));
//    }
//
//}
