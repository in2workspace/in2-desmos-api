package es.in2.desmos.api.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.api.exception.JsonReadingException;
import es.in2.desmos.api.facade.BlockchainToBrokerDataSyncSynchronizer;
import es.in2.desmos.api.facade.BlockchainToBrokerSynchronizer;
import es.in2.desmos.api.facade.BrokerToBlockchainDataSyncPublisher;
import es.in2.desmos.api.facade.BrokerToBlockchainPublisher;
import es.in2.desmos.api.model.BlockchainNotification;
import es.in2.desmos.api.model.Transaction;
import es.in2.desmos.api.model.TransactionStatus;
import es.in2.desmos.api.model.TransactionTrader;
import es.in2.desmos.api.service.TransactionService;
import es.in2.desmos.blockchain.config.properties.BlockchainAdapterProperties;
import es.in2.desmos.blockchain.service.BlockchainAdapterEventPublisher;
import es.in2.desmos.broker.service.BrokerPublicationService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlockchainConnectorInitializer {

    private final TransactionService transactionService;
    private final BlockchainAdapterProperties blockchainAdapterProperties;
    private final BlockchainAdapterEventPublisher blockchainAdapterEventPublisher;
    private final ObjectMapper objectMapper;
    private final BrokerToBlockchainDataSyncPublisher brokerToBlockchainDataSyncPublisher;
    private final BrokerToBlockchainPublisher brokerToBlockchainPublisher;
    private final BlockchainToBrokerSynchronizer blockchainToBrokerSynchronizer;
    private final BlockchainToBrokerDataSyncSynchronizer blockchainToBrokerDataSyncSynchronizer;
    private final BrokerPublicationService brokerPublicationService;

    private Disposable blockchainEventProcessingSubscription;
    private Disposable brokerEntityEventProcessingSubscription;
    private final AtomicBoolean canQueuesEmit = new AtomicBoolean(true);


    @EventListener(ApplicationReadyEvent.class)
    public void processAllTransactions() {
        canQueuesEmit.set(false);
        String processId = UUID.randomUUID().toString();
        MDC.put("processId", processId);
        log.debug("Synchronization tasks started, searching for previous transactions...");
        transactionService.getAllTransactions(processId)
                .collectList()
                .flatMapMany(transactions -> {
                    if (transactions.isEmpty()) {
                        log.info("No previous transactions found, querying DLT Adapter from beginning...");
                        return queryDLTAdapterFromBeginning();
                    } else {
                        return processTransactions(transactions).flux();
                    }
                })
                .then()
                .doOnTerminate(() -> {
                    canQueuesEmit.set(true);
                    log.info("Synchronization tasks finished, enabling Queue Processing...");
                    initializeQueueProcessing();
                    log.info("Queue Processing enabled");
                })
                .subscribe();
    }

    private Mono<Void> processTransactions(List<Transaction> transactions) {
        return Flux.just(findLastTransactionOfType(transactions, TransactionTrader.CONSUMER))
                .flatMap(this::processConsumerTransaction)
                .then(Mono.justOrEmpty(findLastTransactionOfType(transactions, TransactionTrader.PRODUCER))
                        .flatMap(this::processProducerTransaction))
                .then();
    }


    private List<Transaction> findLastTransactionOfType(List<Transaction> transactions, TransactionTrader traderType) {
        return transactions.stream()
                .filter(t -> t.getTrader() == traderType && t.getStatus() == TransactionStatus.PUBLISHED)
                .toList();
    }

    private Mono<Void> processProducerTransaction(List<Transaction> transactions) {
        String timestamp;
        log.debug("Processing Producer Transactions...");
        if (transactions.isEmpty()) {
            log.debug("Empty Transactions");
            timestamp = convertTimestamp("1970-01-01 08:49:01.953");
        } else {
            Transaction lastTransactionPublished = transactions.get(0);
            timestamp = convertTimestamp(lastTransactionPublished.getCreatedAt().toString());
            log.debug(String.valueOf(lastTransactionPublished.getCreatedAt()));
            log.debug(convertTimestamp(lastTransactionPublished.getCreatedAt().toString()));
        }

        return brokerPublicationService.getEntitiesByTimeRange(MDC.get("processId"), timestamp)
                .flatMap(response -> {
                    List<String> ids = extractIdsBasedOnPosition(response);
                    return Flux.fromIterable(ids);
                })
                .flatMap(id -> brokerToBlockchainDataSyncPublisher.createAndSynchronizeBlockchainEvents(MDC.get("processId"), id))
                .then();
    }

    private Flux<Void> processConsumerTransaction(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            log.debug("There are no published transactions. Querying DLT Adapter from beginning...");
            return queryDLTAdapterFromBeginning();
        }
        Transaction lastTransactionPublished = transactions.get(0);
        long lastDateUnixTimestampMillis = lastTransactionPublished.getCreatedAt().toInstant().toEpochMilli();
        long nowUnixTimestampMillis = Instant.now().toEpochMilli();
        return queryDLTAdapterFromRange(lastDateUnixTimestampMillis, nowUnixTimestampMillis);
    }

    private Flux<Void> queryDLTAdapterFromRange(long startDateUnixTimestampMillis, long endDateUnixTimestampMillis) {
        String dltAdapterQueryURL = buildQueryURL(startDateUnixTimestampMillis, endDateUnixTimestampMillis);
        log.debug(dltAdapterQueryURL);

        log.debug("Waiting for events to be processed...");
        return processEvents(startDateUnixTimestampMillis, endDateUnixTimestampMillis);
    }

    private Flux<Void> processEvents(long from, long to) {
        return blockchainAdapterEventPublisher.getEventsFromRange(MDC.get("processId"), from, to)
                .flatMap(this::processResponse)
                .onErrorResume(e -> {
                    log.error("Error sending requests to blockchain node");
                    return Mono.empty();
                });
    }

    private Mono<Void> processResponse(String responseBody) {
        try {
            log.debug("Processing response: {}", responseBody);
            List<BlockchainNotification> events = objectMapper.readValue(responseBody, new TypeReference<>() {});
            return Flux.fromIterable(events)
                    .buffer(50)
                    .flatMap(batch -> Flux.fromIterable(batch)
                                    .flatMap(this::handleEvent)
                                    .subscribeOn(Schedulers.parallel()),
                            10)
                    .then();
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON", e);
            return Mono.error(e);
        }
    }

    private Mono<Void> handleEvent(BlockchainNotification event) {
        log.debug(event.toString());
        String processId = UUID.randomUUID().toString();
        MDC.put("processId", processId);
        return blockchainToBrokerDataSyncSynchronizer.retrieveAndSynchronizeEntityIntoBroker(processId, event);
    }



    private Flux<Void> queryDLTAdapterFromBeginning() {
        long startUnixTimestampMillis = Instant.EPOCH.toEpochMilli();
        long nowUnixTimestampMillis = Instant.now().toEpochMilli();
        String dltAdapterQueryURL = buildQueryURL(startUnixTimestampMillis, nowUnixTimestampMillis);
        log.debug(dltAdapterQueryURL);
        log.debug("Waiting for events to be processed...");
        return processEvents(startUnixTimestampMillis, nowUnixTimestampMillis);
    }

    private String buildQueryURL(long startDateUnixTimestampMillis, long endDateUnixTimestampMillis) {
        return blockchainAdapterProperties.externalDomain() + blockchainAdapterProperties.paths().events() +
                "?startDate=" + startDateUnixTimestampMillis + "&endDate=" + endDateUnixTimestampMillis;
    }

    public static String convertTimestamp(String timestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String truncatedTimestamp = timestamp.substring(0, 19);
            Date parsedDate = inputFormat.parse(truncatedTimestamp);
            LocalDateTime dateTime = LocalDateTime.ofInstant(parsedDate.toInstant(), ZoneId.of("UTC"));
            DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            return dateTime.atZone(ZoneId.of("UTC")).format(outputFormat);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid timestamp");
        }
    }

    public List<String> extractIdsBasedOnPosition(String json) {
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new JsonReadingException("");
        }
        List<String> ids = new ArrayList<>();

        if (rootNode.isArray()) {
            for (JsonNode node : rootNode) {
                JsonNode idNode = node.get("id");
                if (idNode != null) {
                    ids.add(idNode.asText());
                }
            }
        }
        return ids;
    }

    private void initializeQueueProcessing() {
        if (canQueuesEmit.get()) {
            disposeIfActive(blockchainEventProcessingSubscription);
            disposeIfActive(brokerEntityEventProcessingSubscription);

            blockchainEventProcessingSubscription = brokerToBlockchainPublisher.startProcessingEvents()
                    .subscribe(
                            null,
                            error -> log.error("Error occurred during blockchain event processing: {}", error.getMessage(), error),
                            () -> log.info("Blockchain event processing completed")
                    );

            brokerEntityEventProcessingSubscription = blockchainToBrokerSynchronizer.startProcessingEvents()
                    .subscribe(
                            null,
                            error -> log.error("Error occurred during broker entity event processing: {}", error.getMessage(), error),
                            () -> log.info("Broker entity event processing completed")
                    );
        } else {
            log.debug("Queue processing is currently paused.");
        }
    }

    @PreDestroy
    public void cleanUp() {
        disposeIfActive(blockchainEventProcessingSubscription);
        disposeIfActive(brokerEntityEventProcessingSubscription);
    }

    private void disposeIfActive(Disposable subscription) {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }


}



