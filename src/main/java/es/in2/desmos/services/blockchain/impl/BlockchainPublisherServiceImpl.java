package es.in2.desmos.services.blockchain.impl;

import es.in2.desmos.domain.model.*;
import es.in2.desmos.domain.service.AuditRecordService;
import es.in2.desmos.domain.service.QueueService;
import es.in2.desmos.domain.util.ApplicationUtils;
import es.in2.desmos.domain.util.BlockchainDataFactory;
import es.in2.desmos.services.blockchain.BlockchainPublisherService;
import es.in2.desmos.services.blockchain.adapter.BlockchainAdapterService;
import es.in2.desmos.services.blockchain.adapter.factory.BlockchainAdapterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class BlockchainPublisherServiceImpl implements BlockchainPublisherService {

    private final BlockchainAdapterService blockchainAdapterService;
    private final BlockchainDataFactory blockchainDataFactory;
    private final AuditRecordService auditRecordService;
    private final QueueService dataPublicationQueue;

    public BlockchainPublisherServiceImpl(BlockchainAdapterFactory blockchainAdapterFactory, BlockchainDataFactory blockchainDataFactory, AuditRecordService auditRecordService, QueueService dataPublicationQueue) {
        this.blockchainAdapterService = blockchainAdapterFactory.getBlockchainAdapter();
        this.blockchainDataFactory = blockchainDataFactory;
        this.auditRecordService = auditRecordService;
        this.dataPublicationQueue = dataPublicationQueue;
    }

    @Override
    public Flux<Void> publishDataToBlockchain() {
        String processId = UUID.randomUUID().toString();
        log.debug("Process of publishing data into the DLT started with processId: {}", processId);
        // Get the event stream from the data publication queue
        return dataPublicationQueue.getEventStream()
                // Get the first event from the event stream, parse it as a BrokerNotification and filter out null values
                .flatMap(eventQueue -> Mono.just((BrokerNotification) eventQueue.getEvent().get(0))
                        .filter(Objects::nonNull)
                        // Create an event from the BrokerNotification
                        .flatMap(brokerNotification ->
                                // Get the last AuditRecord stored for the same entityId; it is used to calculate the hashLink
                                auditRecordService.fetchLatestProducerEntityHashByEntityId(processId, brokerNotification.data().get(0).get("id").toString())
                                        // Build the BlockchainData object
                                        .flatMap(previousHash ->
                                                blockchainDataFactory.buildBlockchainData(processId, brokerNotification.data().get(0), previousHash))
                                        // Save a new Audit Record with status CREATED
                                        .flatMap(blockchainData ->
                                                auditRecordService.buildAndSaveAuditRecord(processId, brokerNotification.data().get(0), AuditRecordStatus.CREATED, AuditRecordTrader.PRODUCER)
                                                        // Publish the data event to the Blockchain
                                                        .then(blockchainAdapterService.publishEvent(processId, blockchainData))))
                        .doOnSuccess(success -> log.info("Blockchain Event creation and publication completed."))
                        .doOnError(error -> log.error("Error creating or publishing Blockchain Event: {}", error.getMessage(), error)));
    }

}
