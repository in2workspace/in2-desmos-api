package es.in2.desmos.api.service.impl;

import es.in2.desmos.api.model.BlockchainNotification;
import es.in2.desmos.api.model.Transaction;
import es.in2.desmos.api.model.TransactionStatus;
import es.in2.desmos.api.model.TransactionTrader;
import es.in2.desmos.api.service.BrokerEntityRetrievalService;
import es.in2.desmos.api.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static es.in2.desmos.api.util.ApplicationUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerEntityRetrievalServiceImpl implements BrokerEntityRetrievalService {

    private final TransactionService transactionService;

    @Override
    public Mono<String> retrieveEntityFromSourceBroker(String processId, BlockchainNotification blockchainNotification) {
        // Get URL from the DLTNotificationDTO.dataLocation()
        String sourceBrokerEntityURL = extractEntityUrlFromDataLocation(blockchainNotification.dataLocation());
        // Retrieve one of the entities from the broker
        return WebClient.create().get()
                .uri(sourceBrokerEntityURL)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status != null && !hasHlParameter(blockchainNotification.dataLocation())
                                && status.isSameCodeAs(HttpStatusCode.valueOf(404)),
                        clientResponse -> {
                            log.debug("ProcessID: {} - Detected deleted entity notification", processId);
                            return transactionService.saveTransaction(processId, Transaction.builder()
                                            .id(UUID.randomUUID())
                                            .transactionId(processId)
                                            .createdAt(Timestamp.from(Instant.now()))
                                            .dataLocation(blockchainNotification.dataLocation())
                                            .entityId(extractEntityIdFromDataLocation(blockchainNotification.dataLocation()))
                                            .entityHash("")
                                            .status(TransactionStatus.DELETED)
                                            .trader(TransactionTrader.CONSUMER)
                                            .hash("")
                                            .build())
                                    .then(Mono.empty());
                        }
                )
                .onStatus(status -> status != null && status.is2xxSuccessful(),
                        clientResponse -> transactionService.saveTransaction(processId, Transaction.builder()
                                        .id(UUID.randomUUID())
                                        .transactionId(processId)
                                        .createdAt(Timestamp.from(Instant.now()))
                                        .dataLocation(blockchainNotification.dataLocation())
                                        .entityId(extractEntityIdFromDataLocation(blockchainNotification.dataLocation()))
                                        .entityHash("")
                                        .status(TransactionStatus.RETRIEVED)
                                        .trader(TransactionTrader.CONSUMER)
                                        .hash("")
                                        .build())
                                .then(Mono.empty())
                )
                .bodyToMono(String.class);
    }

}


