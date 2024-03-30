//package es.in2.desmos.domain.service.impl;
//
//import es.in2.desmos.domain.model.*;
//import es.in2.desmos.domain.service.BrokerEntityPublisherService;
//import es.in2.desmos.z.services.TransactionService;
//import es.in2.desmos.domain.util.ApplicationUtils;
//import es.in2.desmos.z.services.BrokerPublicationService;
//import es.in2.desmos.z.domain.model.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//import java.security.NoSuchAlgorithmException;
//import java.sql.Timestamp;
//import java.time.Instant;
//import java.util.UUID;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class BrokerEntityPublisherServiceImpl implements BrokerEntityPublisherService {
//
//    private final TransactionService transactionService;
//    private final BrokerPublicationService brokerPublicationService;
//
//    @Override
//    public Mono<Void> publishRetrievedEntityToBroker(String processId, String retrievedBrokerEntity,
//                                                     DLTNotification dltNotification) {
//        // extract entity id from data location
//        String entityId = ApplicationUtils.extractEntityIdFromDataLocation(dltNotification.dataLocation());
//        // Check if the entity is deleted
//        if (retrievedBrokerEntity.contains("errorCode")) {
//            log.debug("ProcessID: {} - Detected deleted entity notification", processId);
//            // delete entity from Broker
//            return brokerPublicationService.deleteEntityById(processId, entityId).onErrorResume(
//                            error -> {
//                                log.error("ProcessID: {} - Error deleting entity", processId);
//                                return transactionService.saveFailedEntityTransaction(processId, FailedEntityTransaction.builder()
//                                        .id(UUID.randomUUID())
//                                        .transactionId(processId)
//                                        .notificationId(dltNotification.id())
//                                        .createdAt(Timestamp.from(Instant.now()))
//                                        .entityId(entityId)
//                                        .entityType(dltNotification.eventType()).datalocation(dltNotification.dataLocation()).priority(EventQueuePriority.RECOVER_DELETE)
//                                        .previousEntityHash(dltNotification.previousEntityHash())
//                                        .entity(retrievedBrokerEntity)
//                                        .newTransaction(true)
//                                        .build());
//                            })
//                    .then(transactionService.saveTransaction(processId, Transaction.builder()
//                            .id(UUID.randomUUID())
//                            .transactionId(processId)
//                            .createdAt(Timestamp.from(Instant.now()))
//                            .entityId(entityId)
//                            .entityType(dltNotification.eventType())
//                            .entityHash(ApplicationUtils.extractHashLinkFromDataLocation(dltNotification.dataLocation()))
//                            .datalocation(dltNotification.dataLocation())
//                            .status(TransactionStatus.DELETED)
//                            .trader(TransactionTrader.CONSUMER)
//                            .newTransaction(true)
//                            .build()));
//        } else {
//            log.debug("ProcessID: {} - Detected entity notification", processId);
//            // Create Hash from the retrieved entity
//            try {
//                String entityHash = ApplicationUtils.calculateSHA256(retrievedBrokerEntity);
//                String previousHash = dltNotification.previousEntityHash();
//                String intertwinedHash = previousHash.equals("0x0000000000000000000000000000000000000000000000000000000000000000") ?
//                        entityHash :
//                        ApplicationUtils.calculateHashLink(entityHash, previousHash);
//                String sourceEntityHash = ApplicationUtils.extractHashLinkFromDataLocation(dltNotification.dataLocation());
//                log.debug("entityHash: {}", entityHash);
//                log.debug("previousHash: {}", previousHash);
//                log.debug("intertwinedHash: {}", intertwinedHash);
//                log.debug("sourceEntityHash : {}", sourceEntityHash);
//                if (intertwinedHash.equals(sourceEntityHash)) {
//                    log.debug("ProcessID: {} - Entity integrity is valid", processId);
//                    // publish or update entity to Broker
//                    return brokerPublicationService.getEntityById(processId, entityId)
//                            .flatMap(response -> {
//                                if (response.contains("errorCode")) {
//                                    log.info("ProcessID: {} - Entity doesn't exist", processId);
//                                    return brokerPublicationService.postEntity(processId, retrievedBrokerEntity).onErrorResume(
//                                            error -> {
//                                                log.error("ProcessID: {} - Error publishing entity", processId);
//                                                return transactionService.saveFailedEntityTransaction(processId,
//                                                        FailedEntityTransaction.builder()
//                                                                .id(UUID.randomUUID())
//                                                                .transactionId(processId)
//                                                                .notificationId(dltNotification.id())
//                                                                .createdAt(Timestamp.from(Instant.now()))
//                                                                .entityId(entityId)
//                                                                .entityType(dltNotification.eventType()).datalocation(dltNotification.dataLocation()).priority(EventQueuePriority.RECOVER_PUBLISH)
//                                                                .previousEntityHash(dltNotification.previousEntityHash())
//                                                                .entity(retrievedBrokerEntity)
//                                                                .newTransaction(true)
//                                                                .build());
//                                            });
//                                } else {
//                                    log.info("ProcessId: {} - Entity exists", processId);
//                                    return brokerPublicationService.updateEntity(processId, retrievedBrokerEntity).onErrorResume(
//                                            error -> {
//                                                log.error("ProcessID: {} - Error updating entity", processId);
//                                                return transactionService.saveFailedEntityTransaction(processId,
//                                                        FailedEntityTransaction.builder()
//                                                                .id(UUID.randomUUID())
//                                                                .notificationId(dltNotification.id())
//                                                                .transactionId(processId)
//                                                                .createdAt(Timestamp.from(Instant.now()))
//                                                                .entityId(entityId)
//                                                                .entityType(dltNotification.eventType()).datalocation(dltNotification.dataLocation()).priority(EventQueuePriority.RECOVER_EDIT)
//                                                                .previousEntityHash(dltNotification.previousEntityHash())
//                                                                .entity(retrievedBrokerEntity)
//                                                                .newTransaction(true)
//                                                                .build());
//                                            }
//                                    );
//                                }
//                            }).onErrorResume(
//                                    error -> {
//                                        log.info("ProcessID: {} - Entity doesn't exist", processId);
//                                        return brokerPublicationService.postEntity(processId, retrievedBrokerEntity);
//                                    }
//                            )
//                            // This function SHOULD be changed when intertwined hash will be implemented
//                            .then(transactionService.saveTransaction(processId, Transaction.builder()
//                                    .id(UUID.randomUUID())
//                                    .transactionId(processId)
//                                    .createdAt(Timestamp.from(Instant.now()))
//                                    .entityId(entityId)
//                                    .entityType(dltNotification.eventType())
//                                    .entityHash(entityHash)
//                                    .datalocation(dltNotification.dataLocation())
//                                    .status(TransactionStatus.PUBLISHED)
//                                    .trader(TransactionTrader.CONSUMER)
//                                    .newTransaction(true)
//                                    .build()));
//                } else {
//                    log.error("ProcessID: {} - Entity integrity is not valid", processId);
//                    return Mono.error(new IllegalArgumentException("Entity integrity cannot be validated"));
//                }
//            } catch (NoSuchAlgorithmException e) {
//                log.error("Error validating entity integrity: {}", e.getMessage(), e.getCause());
//                return Mono.error(e);
//            }
//        }
//    }
//
//}
