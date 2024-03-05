package es.in2.desmos.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.api.exception.BrokerNotificationParserException;
import es.in2.desmos.api.model.*;
import es.in2.desmos.api.service.NotificationProcessorService;
import es.in2.desmos.api.service.QueueService;
import es.in2.desmos.api.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static es.in2.desmos.api.util.ApplicationUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProcessorServiceImpl implements NotificationProcessorService {

    private final ObjectMapper objectMapper;
    private final TransactionService transactionService;
    private final QueueService brokerToBlockchainQueueService;


    @Override
    public Mono<Void> detectBrokerNotificationPriority(String processId, BrokerNotification brokerNotification) {
        return validateBrokerNotification(brokerNotification)
                .flatMap(dataMap -> {
                    if (dataMap.containsKey("deletedAt")) {
                        return Mono.just(EventQueuePriority.PUBLICATIONDELETE);
                    } else {
                        return transactionService.findLatestPublishedOrDeletedTransactionForEntity(processId, dataMap.get("id").toString())
                                .flatMap(transaction ->
                                        Mono.just(EventQueuePriority.PUBLICATIONEDIT)
                                )
                                .defaultIfEmpty(EventQueuePriority.PUBLICATIONPUBLISH);
                    }
                }).flatMap(eventQueuePriority -> brokerToBlockchainQueueService.enqueueEvent(EventQueue.builder()
                        .event(Collections.singletonList(brokerNotification))
                        .priority(eventQueuePriority)
                        .build())).then();
    }

    @Override
    public Mono<Void> detectBlockchainNotificationPriority(String processId, BlockchainNotification blockchainNotification) {
        checkIfNotificationIsNullOrDataLocationIsEmpty(blockchainNotification);
        EventQueuePriority eventQueuePriority = EventQueuePriority.PUBLICATIONPUBLISH;
        if (!hasHlParameter(blockchainNotification.dataLocation())) {
            eventQueuePriority = EventQueuePriority.PUBLICATIONDELETE;
        } else if (!Objects.equals(blockchainNotification.previousEntityHash(), "0x0000000000000000000000000000000000000000000000000000000000000000")) {
            eventQueuePriority = EventQueuePriority.PUBLICATIONEDIT;
        }

        return brokerToBlockchainQueueService.enqueueEvent(EventQueue.builder()
                .event(Collections.singletonList(blockchainNotification))
                .priority(eventQueuePriority)
                .build()).then();
    }




    @Override
    public Mono<Map<String, Object>> processBrokerNotification(String processId, BrokerNotification brokerNotification) {
        // validateBrokerNotification
        return validateBrokerNotification(brokerNotification)
                // isBrokerNotificationFromExternalSource || verifyBrokerNotificationIsNotSelfGenerated
                .flatMap(dataMap -> isBrokerNotificationFromExternalSource(processId, dataMap));
    }

    private Mono<Map<String, Object>> validateBrokerNotification(BrokerNotification brokerNotification) {
        // Validate brokerNotification
        checkIfBrokerNotificationIsNullOrEmpty(brokerNotification);
        // Validate brokerNotification data
        Map<String, Object> dataMap = brokerNotification.data().get(0);
        checkIfBrokerNotificationDataIsNullOrHasNullId(dataMap);
        return Mono.just(dataMap);
    }

    private void checkIfBrokerNotificationIsNullOrEmpty(BrokerNotification brokerNotification) {
        if (brokerNotification == null || brokerNotification.data().isEmpty()) {
            throw new IllegalArgumentException("Invalid BrokerNotificationDTO");
        }
    }

    private void checkIfBrokerNotificationDataIsNullOrHasNullId(Map<String, Object> dataMap) {
        if (dataMap == null || dataMap.get("id") == null) {
            throw new IllegalArgumentException("Invalid dataMap in BrokerNotificationDTO");
        }
    }

    private Mono<Map<String, Object>> isBrokerNotificationFromExternalSource(String processId, Map<String, Object> dataMap) {
        return transactionService.findLatestPublishedOrDeletedTransactionForEntity(processId, dataMap.get("id").toString())
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .flatMap(optionalTransaction -> {
                    if (optionalTransaction.isEmpty()) {
                        log.debug("ProcessID: {} - No transaction found; assuming BrokerNotification is from external source", processId);
                        return Mono.just(dataMap);
                    }

                    Transaction transactionFound = optionalTransaction.get();
                    try {
                        String brokerEntityAsString = objectMapper.writer().writeValueAsString(dataMap);
                        String brokerEntityHash = calculateSHA256Hash(brokerEntityAsString);
                        if (transactionFound.getEntityHash().equals(brokerEntityHash)) {
                            log.debug("ProcessID: {} - BrokerNotification is self-generated", processId);
                            return Mono.empty();
                        } else {
                            log.debug("ProcessID: {} - BrokerNotification is from external source", processId);
                            return Mono.just(dataMap);
                        }
                    } catch (JsonProcessingException | NoSuchAlgorithmException e) {
                        return Mono.error(new BrokerNotificationParserException("Error processing JSON", e));
                    }
                });
    }


    @Override
    public Mono<Void> processBlockchainNotification(String processId, BlockchainNotification blockchainNotification) {
        // Validate input
        checkIfNotificationIsNullOrDataLocationIsEmpty(blockchainNotification);
        // Build and save transaction
        return transactionService.saveTransaction(processId, Transaction.builder()
                        .id(UUID.randomUUID())
                        .transactionId(processId)
                        .createdAt(Timestamp.from(Instant.now()))
                        .datalocation(blockchainNotification.dataLocation())
                        .entityId(extractEntityIdFromDataLocation(blockchainNotification.dataLocation()))
                        .entityType(blockchainNotification.eventType())
                        .entityHash(extractEntityHashFromDataLocation(blockchainNotification.dataLocation()))
                        .status(TransactionStatus.RECEIVED)
                        .trader(TransactionTrader.CONSUMER)
                        .newTransaction(true)
                        .build());
    }

    private void checkIfNotificationIsNullOrDataLocationIsEmpty(BlockchainNotification blockchainNotification) {
        if (blockchainNotification == null || blockchainNotification.dataLocation().isEmpty()) {
            throw new IllegalArgumentException("Invalid Blockchain Notification");
        }
    }

}
