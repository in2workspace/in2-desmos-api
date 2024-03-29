package es.in2.desmos.application.service.impl;

import es.in2.desmos.application.service.DataPublicationService;
import es.in2.desmos.domain.model.BrokerNotification;
import es.in2.desmos.domain.service.AuditRecordService;
import es.in2.desmos.domain.service.DLTEventCreatorService;
import es.in2.desmos.domain.service.QueueService;
import es.in2.desmos.infrastructure.blockchain.service.DLTAdapterPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataPublicationServiceImpl implements DataPublicationService {

    private final QueueService dataPublicationQueue;
    private final DLTEventCreatorService dltEventCreatorService;
    private final DLTAdapterPublisher dltAdapterPublisher;
    private final AuditRecordService auditRecordService;

    public Flux<Void> startPublishingDataToDLT() {
        String processId = UUID.randomUUID().toString();
        log.debug("Process of publishing data into the DLT started with processId: {}", processId);
        // Get the event stream from the data publication queue
        return dataPublicationQueue.getEventStream()
                // Get the first event from the event stream, parse it as a BrokerNotification and filter out null values
                .flatMap(eventQueue -> Mono.just((BrokerNotification) eventQueue.getEvent().get(0))
                        .filter(Objects::nonNull)
                        // Create an event from the BrokerNotification
                        .flatMap(brokerNotification -> auditRecordService.fetchLatestProducerEntityHashByEntityId(processId,
                                    brokerNotification.data().get(0).get("id").toString())
                                        .flatMap(previousHash ->
                                                dltEventCreatorService.buildDLTEvent(processId, brokerNotification.data().get(0), previousHash)
                                        )
                        )
                        // Publish the data event to the DLT
                        .flatMap(blockchainEvent ->
                                dltAdapterPublisher.publishBlockchainEvent(processId, blockchainEvent))
                        .doOnSuccess(success -> log.info("Blockchain Event creation and publication completed."))
                        .doOnError(error -> log.error("Error creating or publishing Blockchain Event: {}", error.getMessage(), error)));
    }

}
