package es.in2.desmos.services.broker.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exceptions.JsonReadingException;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.services.QueueService;
import es.in2.desmos.domain.utils.ApplicationUtils;
import es.in2.desmos.services.broker.BrokerPublisherService;
import es.in2.desmos.services.broker.adapter.BrokerAdapterService;
import es.in2.desmos.services.broker.adapter.factory.BrokerAdapterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class BrokerPublisherServiceImpl implements BrokerPublisherService {

    private final ObjectMapper objectMapper;
    private final BrokerAdapterService brokerAdapter;
    private final QueueService dataRetrievalQueue;

    public BrokerPublisherServiceImpl(ObjectMapper objectMapper, BrokerAdapterFactory brokerAdapterFactory, QueueService dataRetrievalQueue) {
        this.objectMapper = objectMapper;
        this.brokerAdapter = brokerAdapterFactory.getBrokerAdapter();
        this.dataRetrievalQueue = dataRetrievalQueue;
    }

    @Override
    public Flux<Void> publishDataToBroker() {
        String processId = UUID.randomUUID().toString();
        log.debug("Process of retrieving data from the external sources started with processId: {}", processId);
        // The dataRetrievalQueue is a QueueService object used to retrieve data from the external sources
        return dataRetrievalQueue.getEventStream()
                // get the next DLTNotification from the queue
                .flatMap(eventQueue -> Mono.just((BlockchainNotification) eventQueue.getEvent().get(0))
                        // verify that the DLTNotification is not null
                        .filter(Objects::nonNull)
                        // retrieve the entity from the source broker
                        .flatMap(dltNotification -> retrieveEntityFromSourceBroker(processId, dltNotification)
                                        // validate and publish the retrieved entity
                                        .flatMap(retrievedEntity -> publishRetrievedEntityToBroker(processId, retrievedEntity, dltNotification))
                        )
                        .doOnSuccess(voidValue -> log.info("ProcessID: {} - Entity retrieval, validation, and publication completed", processId))
                        .doOnError(e -> log.error("ProcessID: {} - Error retrieving, validating, and publishing entity", processId)));
    }


//    public Mono<Void> postEntity(String processId, String requestBody) {
//        return brokerAdapter.postEntity(processId, requestBody);
//    }
//
//    public Mono<String> getEntityById(String processId, String entityId) {
//        return brokerAdapter.getEntityById(processId, entityId);
//    }
//
//    public Flux<String> getEntitiesByTimeRange(String processId, String timestamp) {
//        return brokerAdapter.getEntitiesByTimeRange(processId, timestamp);
//    }
//
//    public Mono<Void> updateEntity(String processId, String requestBody) {
//        return brokerAdapter.updateEntity(processId, requestBody);
//    }
//
//    public Mono<Void> deleteEntityById(String processId, String entityId) {
//        return brokerAdapter.deleteEntityById(processId, entityId);
//    }

    public Mono<Void> publishRetrievedEntityToBroker(String processId, String retrievedBrokerEntity,
                                                     BlockchainNotification blockchainNotification) {
        // extract entity id from data location
        String entityId = ApplicationUtils.extractEntityIdFromDataLocation(blockchainNotification.dataLocation());
        // Check if the entity is deleted
        if (retrievedBrokerEntity.contains("errorCode")) {
            log.debug("ProcessID: {} - Detected deleted entity notification", processId);
            // delete entity from Broker
            return brokerAdapter.deleteEntityById(processId, entityId).onErrorResume(
                            error -> {
                                log.error("ProcessID: {} - Error deleting entity", processId);
                                // fixme:
//                                return transactionService.saveFailedEntityTransaction(processId, FailedEntityTransaction.builder()
//                                        .id(UUID.randomUUID())
//                                        .transactionId(processId)
//                                        .notificationId(blockchainNotification.id())
//                                        .createdAt(Timestamp.from(Instant.now()))
//                                        .entityId(entityId)
//                                        .entityType(blockchainNotification.eventType()).datalocation(blockchainNotification.dataLocation()).priority(EventQueuePriority.RECOVER_DELETE)
//                                        .previousEntityHash(blockchainNotification.previousEntityHash())
//                                        .entity(retrievedBrokerEntity)
//                                        .newTransaction(true)
//                                        .build());
                                // todo: fixme
                                return Mono.empty();
                            })
                    .then(
                            // fixme:
//                            transactionService.saveTransaction(processId, Transaction.builder()
//                            .id(UUID.randomUUID())
//                            .transactionId(processId)
//                            .createdAt(Timestamp.from(Instant.now()))
//                            .entityId(entityId)
//                            .entityType(blockchainNotification.eventType())
//                            .entityHash(ApplicationUtils.extractHashLinkFromDataLocation(blockchainNotification.dataLocation()))
//                            .datalocation(blockchainNotification.dataLocation())
//                            .status(TransactionStatus.DELETED)
//                            .trader(TransactionTrader.CONSUMER)
//                            .newTransaction(true)
//                            .build())
                            // todo: fixme
                            Mono.empty()
                    );
        } else {
            log.debug("ProcessID: {} - Detected entity notification", processId);
            // Create Hash from the retrieved entity
            try {
                String entityHash = ApplicationUtils.calculateSHA256(retrievedBrokerEntity);
                String previousHash = blockchainNotification.previousEntityHash();
                String intertwinedHash = previousHash.equals("0x0000000000000000000000000000000000000000000000000000000000000000") ?
                        entityHash :
                        ApplicationUtils.calculateHashLink(entityHash, previousHash);
                String sourceEntityHash = ApplicationUtils.extractHashLinkFromDataLocation(blockchainNotification.dataLocation());
                log.debug("entityHash: {}", entityHash);
                log.debug("previousHash: {}", previousHash);
                log.debug("intertwinedHash: {}", intertwinedHash);
                log.debug("sourceEntityHash : {}", sourceEntityHash);
                if (intertwinedHash.equals(sourceEntityHash)) {
                    log.debug("ProcessID: {} - Entity integrity is valid", processId);
                    // publish or update entity to Broker
                    return brokerAdapter.getEntityById(processId, entityId)
                            .flatMap(response -> {
                                if (response.contains("errorCode")) {
                                    log.info("ProcessID: {} - Entity doesn't exist", processId);
                                    return brokerAdapter.postEntity(processId, retrievedBrokerEntity).onErrorResume(
                                            error -> {
                                                log.error("ProcessID: {} - Error publishing entity", processId);
                                                // fixme
//                                                return transactionService.saveFailedEntityTransaction(processId,
//                                                        FailedEntityTransaction.builder()
//                                                                .id(UUID.randomUUID())
//                                                                .transactionId(processId)
//                                                                .notificationId(blockchainNotification.id())
//                                                                .createdAt(Timestamp.from(Instant.now()))
//                                                                .entityId(entityId)
//                                                                .entityType(blockchainNotification.eventType()).datalocation(blockchainNotification.dataLocation()).priority(EventQueuePriority.RECOVER_PUBLISH)
//                                                                .previousEntityHash(blockchainNotification.previousEntityHash())
//                                                                .entity(retrievedBrokerEntity)
//                                                                .newTransaction(true)
//                                                                .build());
                                                // todo: fixme
                                                return Mono.empty();
                                            });
                                } else {
                                    log.info("ProcessId: {} - Entity exists", processId);
                                    return brokerAdapter.updateEntity(processId, retrievedBrokerEntity).onErrorResume(
                                            error -> {
                                                log.error("ProcessID: {} - Error updating entity", processId);
                                                // fixme
//                                                return transactionService.saveFailedEntityTransaction(processId,
//                                                        FailedEntityTransaction.builder()
//                                                                .id(UUID.randomUUID())
//                                                                .notificationId(blockchainNotification.id())
//                                                                .transactionId(processId)
//                                                                .createdAt(Timestamp.from(Instant.now()))
//                                                                .entityId(entityId)
//                                                                .entityType(blockchainNotification.eventType()).datalocation(blockchainNotification.dataLocation()).priority(EventQueuePriority.RECOVER_EDIT)
//                                                                .previousEntityHash(blockchainNotification.previousEntityHash())
//                                                                .entity(retrievedBrokerEntity)
//                                                                .newTransaction(true)
//                                                                .build());
                                                // todo: fixme
                                                return Mono.empty();
                                            }
                                    );
                                }
                            }).onErrorResume(
                                    error -> {
                                        log.info("ProcessID: {} - Entity doesn't exist", processId);
                                        return brokerAdapter.postEntity(processId, retrievedBrokerEntity);
                                    }
                            )
                            // This function SHOULD be changed when intertwined hash will be implemented
                            .then(
                                    // fixme
//                                    transactionService.saveTransaction(processId, Transaction.builder()
//                                    .id(UUID.randomUUID())
//                                    .transactionId(processId)
//                                    .createdAt(Timestamp.from(Instant.now()))
//                                    .entityId(entityId)
//                                    .entityType(blockchainNotification.eventType())
//                                    .entityHash(entityHash)
//                                    .datalocation(blockchainNotification.dataLocation())
//                                    .status(TransactionStatus.PUBLISHED)
//                                    .trader(TransactionTrader.CONSUMER)
//                                    .newTransaction(true)
//                                    .build())
                                    // todo: fixme
                                    Mono.empty()
                            );
                } else {
                    log.error("ProcessID: {} - Entity integrity is not valid", processId);
                    return Mono.error(new IllegalArgumentException("Entity integrity cannot be validated"));
                }
            } catch (NoSuchAlgorithmException e) {
                log.error("Error validating entity integrity: {}", e.getMessage(), e.getCause());
                return Mono.error(e);
            }
        }
    }

    public Mono<Map<String, Object>> processBrokerEntity(String processId, String brokerEntityId) {
        return brokerAdapter.getEntityById(processId, brokerEntityId)
                .flatMap(response -> {
                    try {
                        Map<String, Object> dataMap = objectMapper.readValue(response, new TypeReference<>() {
                        });
                        return Mono.just(dataMap);
                    } catch (JsonProcessingException e) {
                        return Mono.error(new JsonReadingException("Error while processing entities."));
                    }
                })
                .doOnSuccess(dataMap -> log.info("Broker Entity processed successfully."))
                .doOnError(error -> log.error("Error processing Broker Entity: {}", error.getMessage(), error));
    }

    public Mono<String> retrieveEntityFromSourceBroker(String processId, BlockchainNotification blockchainNotification) {
        // Get URL from the DLTNotificationDTO.dataLocation()
        String sourceBrokerEntityURL = ApplicationUtils.extractContextBrokerUrlFromDataLocation(blockchainNotification.dataLocation());
        // Retrieve one of the entities from the broker
        return WebClient.create().get()
                .uri(sourceBrokerEntityURL)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status != null && !ApplicationUtils.checkIfHashLinkExistInDataLocation(blockchainNotification.dataLocation())
                                && status.isSameCodeAs(HttpStatusCode.valueOf(404)),
                        clientResponse -> {
                            log.debug("ProcessID: {} - Detected deleted entity notification", processId);
                            // fixme
//                            return transactionService.saveTransaction(processId, Transaction.builder()
//                                            .id(UUID.randomUUID())
//                                            .transactionId(processId)
//                                            .createdAt(Timestamp.from(Instant.now()))
//                                            .datalocation(blockchainNotification.dataLocation())
//                                            .entityId(ApplicationUtils.extractEntityIdFromDataLocation(blockchainNotification.dataLocation()))
//                                            .entityHash(ApplicationUtils.extractHashLinkFromDataLocation(blockchainNotification.dataLocation()))
//                                            .status(TransactionStatus.DELETED)
//                                            .trader(TransactionTrader.CONSUMER)
//                                            .newTransaction(true)
//                                            .build())
//                                    .then(Mono.empty());
                            // todo: fixme
                            return Mono.empty();
                        }
                )
                .onStatus(status -> status != null && status.is2xxSuccessful(),
                        clientResponse ->
                                // fixme
//                                transactionService.saveTransaction(processId, Transaction.builder()
//                                        .id(UUID.randomUUID())
//                                        .transactionId(processId)
//                                        .createdAt(Timestamp.from(Instant.now()))
//                                        .datalocation(blockchainNotification.dataLocation())
//                                        .entityId(ApplicationUtils.extractEntityIdFromDataLocation(blockchainNotification.dataLocation()))
//                                        .entityHash(ApplicationUtils.extractHashLinkFromDataLocation(blockchainNotification.dataLocation()))
//                                        .entityType(blockchainNotification.eventType())
//                                        .status(TransactionStatus.RETRIEVED)
//                                        .trader(TransactionTrader.CONSUMER)
//                                        .newTransaction(true)
//                                        .build())
//                                .then(Mono.empty()
                                // todo: fixme
                                Mono.empty()
                )
                .bodyToMono(String.class);
    }

}
