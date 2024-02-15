package es.in2.desmos.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.api.exception.BrokerNotificationParserException;
import es.in2.desmos.api.model.*;
import es.in2.desmos.api.service.NotificationProcessorService;
import es.in2.desmos.api.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static es.in2.desmos.api.util.ApplicationUtils.calculateSHA256Hash;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProcessorServiceImpl implements NotificationProcessorService {

    private final ObjectMapper objectMapper;
    private final TransactionService transactionService;

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
        // isBrokerNotificationFromExternalSource
        return transactionService.findLatestPublishedOrDeletedTransactionForEntity(processId, dataMap.get("id").toString())
                .flatMap(transactionFound -> {
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
                }).switchIfEmpty(Mono.defer(() -> {
                    log.debug("ProcessID: {} - No transaction found; assuming BrokerNotification is from external source", processId);
                    return Mono.just(dataMap);
                }));
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
                        .dataLocation(blockchainNotification.dataLocation())
                        .entityId("")
                        .entityType(blockchainNotification.eventType())
                        .entityHash("")
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
