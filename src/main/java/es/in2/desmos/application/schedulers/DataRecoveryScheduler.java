//package es.in2.desmos.schedulers;
//
//import es.in2.desmos.domain.model.BlockchainData;
//import es.in2.desmos.domain.model.BlockchainNotification;
//import es.in2.desmos.domain.model.EventQueue;
//import es.in2.desmos.domain.service.QueueService;
//import es.in2.desmos.z.services.TransactionService;
//import es.in2.desmos.domain.util.ApplicationUtils;
//import es.in2.desmos.domain.exception.HashCreationException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.MDC;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
//import java.security.NoSuchAlgorithmException;
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class DataRecoveryScheduler {
//
//    private final QueueService dataPublicationQueue;
//    private final QueueService dataRetrievalQueue;
//    private final TransactionService transactionService;
//
//    @Scheduled(fixedRate = 60000)
//    public void processFailedEvents() {
//        String processId = UUID.randomUUID().toString();
//        MDC.put("processId", processId);
//        log.debug("Retrying failed events...");
//
//        transactionService.getAllFailedEventTransactions(processId)
//                .flatMap(event ->
//                        transactionService.deleteFailedEventTransaction(processId, event.getId())
//                                .then(Mono.defer(() -> {
//                                    BlockchainData blkEvent;
//                                    try {
//                                        blkEvent = BlockchainData.builder()
//                                                .eventType(event.getEntityType())
//                                                .dataLocation(event.getDatalocation())
//                                                .entityId(ApplicationUtils.HASH_PREFIX + ApplicationUtils.calculateSHA256(event.getEntityId()))
//                                                .previousEntityHash(event.getPreviousEntityHash())
//                                                .organizationIdentifier(event.getOrganizationId())
//                                                .metadata(List.of())
//                                                .build();
//                                    } catch (NoSuchAlgorithmException e) {
//                                        throw new HashCreationException("Error creating hash");
//                                    }
//                                    return dataPublicationQueue.enqueueEvent(EventQueue.builder()
//                                            .event(Collections.singletonList(blkEvent))
//                                            .priority(event.getPriority())
//                                            .build());
//                                }))
//                ).switchIfEmpty(
//                        Mono.defer(() -> {
//                            log.debug("No failed events to process");
//                            return Mono.empty();
//                        })
//                )
//                .subscribe(
//                        success -> log.debug("Event retried successfully"),
//                        error -> log.debug("Error retrying event")
//                );
//    }
//
//    @Scheduled(fixedRate = 60000)
//    public void processFailedEntities() {
//        String processId = UUID.randomUUID().toString();
//        MDC.put("processId", processId);
//        log.debug("Retrying failed entities...");
//
//        transactionService.getAllFailedEntityTransactions(processId)
//                .flatMap(entity ->
//                        transactionService.deleteFailedEntityTransaction(processId, entity.getId())
//                                .then(Mono.defer(() -> {
//                                    BlockchainNotification blkNotification = BlockchainNotification.builder()
//                                            .id(entity.getNotificationId())
//                                            .dataLocation(entity.getDatalocation())
//                                            .timestamp(entity.getTimestamp())
//                                            .eventType(entity.getEntityType())
//                                            .entityId(entity.getEntityId())
//                                            .previousEntityHash(entity.getPreviousEntityHash())
//                                            .build();
//                                    return dataRetrievalQueue.enqueueEvent(EventQueue.builder()
//                                            .event(List.of(blkNotification, entity.getEntity()))
//                                            .priority(entity.getPriority())
//                                            .build());
//                                }))
//                ).switchIfEmpty(
//                        Mono.defer(() -> {
//                            log.debug("No failed entities to process");
//                            return Mono.empty();
//                        })
//                )
//                .subscribe(
//                        success -> log.debug("Entity retried successfully"),
//                        error -> log.debug("Error retrying entity")
//                );
//    }
//
//}
