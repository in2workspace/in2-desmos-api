//package es.in2.desmos.domain.service.impl;
//
//import es.in2.desmos.domain.model.DLTNotification;
//import es.in2.desmos.domain.model.Transaction;
//import es.in2.desmos.domain.model.TransactionStatus;
//import es.in2.desmos.domain.model.TransactionTrader;
//import es.in2.desmos.domain.service.BrokerEntityRetrievalService;
//import es.in2.desmos.z.services.TransactionService;
//import es.in2.desmos.domain.util.ApplicationUtils;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import java.sql.Timestamp;
//import java.time.Instant;
//import java.util.UUID;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class BrokerEntityRetrievalServiceImpl implements BrokerEntityRetrievalService {
//
//    private final TransactionService transactionService;
//
//    @Override
//    public Mono<String> retrieveEntityFromSourceBroker(String processId, DLTNotification dltNotification) {
//        // Get URL from the DLTNotificationDTO.dataLocation()
//        String sourceBrokerEntityURL = ApplicationUtils.extractContextBrokerUrlFromDataLocation(dltNotification.dataLocation());
//        // Retrieve one of the entities from the broker
//        return WebClient.create().get()
//                .uri(sourceBrokerEntityURL)
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .onStatus(status -> status != null && !ApplicationUtils.checkIfHashLinkExistInDataLocation(dltNotification.dataLocation())
//                                && status.isSameCodeAs(HttpStatusCode.valueOf(404)),
//                        clientResponse -> {
//                            log.debug("ProcessID: {} - Detected deleted entity notification", processId);
//                            return transactionService.saveTransaction(processId, Transaction.builder()
//                                            .id(UUID.randomUUID())
//                                            .transactionId(processId)
//                                            .createdAt(Timestamp.from(Instant.now()))
//                                            .datalocation(dltNotification.dataLocation())
//                                            .entityId(ApplicationUtils.extractEntityIdFromDataLocation(dltNotification.dataLocation()))
//                                            .entityHash(ApplicationUtils.extractHashLinkFromDataLocation(dltNotification.dataLocation()))
//                                            .status(TransactionStatus.DELETED)
//                                            .trader(TransactionTrader.CONSUMER)
//                                            .newTransaction(true)
//                                            .build())
//                                    .then(Mono.empty());
//                        }
//                )
//                .onStatus(status -> status != null && status.is2xxSuccessful(),
//                        clientResponse -> transactionService.saveTransaction(processId, Transaction.builder()
//                                        .id(UUID.randomUUID())
//                                        .transactionId(processId)
//                                        .createdAt(Timestamp.from(Instant.now()))
//                                        .datalocation(dltNotification.dataLocation())
//                                        .entityId(ApplicationUtils.extractEntityIdFromDataLocation(dltNotification.dataLocation()))
//                                        .entityHash(ApplicationUtils.extractHashLinkFromDataLocation(dltNotification.dataLocation()))
//                                        .entityType(dltNotification.eventType())
//                                        .status(TransactionStatus.RETRIEVED)
//                                        .trader(TransactionTrader.CONSUMER)
//                                        .newTransaction(true)
//                                        .build())
//                                .then(Mono.empty())
//                )
//                .bodyToMono(String.class);
//    }
//
//}
//
//
