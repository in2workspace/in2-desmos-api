package es.in2.desmos.services.blockchain.adapter.impl;

import es.in2.desmos.configs.properties.DLTAdapterProperties;
import es.in2.desmos.domain.model.*;
import es.in2.desmos.services.blockchain.adapter.BlockchainAdapterService;
import es.in2.desmos.z.services.TransactionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableRetry
public class BlockchainAdapterServiceImpl implements BlockchainAdapterService {

    private final DLTAdapterProperties dltAdapterProperties;
    private final TransactionService transactionService;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(dltAdapterProperties.internalDomain())
                .build();
    }

    @Override
    public Mono<Void> createSubscription(String processId, BlockchainSubscription blockchainSubscription) {
        log.info("ProcessId: {} - Creating subscription...", processId);
        return webClient.post()
                .uri(dltAdapterProperties.paths()
                        .subscription())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(blockchainSubscription)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Void> publishEvent(String processId, BlockchainData blockchainData) {
        return webClient.post()
                .uri(dltAdapterProperties.paths()
                        .publication())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(blockchainData)
                .retrieve()
                .onStatus(status -> status != null && status.is2xxSuccessful(),
                        response -> {
                            // FIXME: This has business logic, it should be moved to the service layer
                            if (!checkIfHashLinkExistInDataLocation(blockchainData.dataLocation())) {
                                return transactionService.saveTransaction(processId, Transaction.builder()
                                                .id(UUID.randomUUID())
                                                .transactionId(processId)
                                                .createdAt(Timestamp.from(Instant.now()))
                                                .entityId(extractEntityIdFromDataLocation(blockchainData.dataLocation()))
                                                .entityType(blockchainData.eventType())
                                                .entityHash(extractHashLinkFromDataLocation(blockchainData.dataLocation()))
                                                .status(TransactionStatus.DELETED)
                                                .trader(TransactionTrader.PRODUCER)
                                                .datalocation(blockchainData.dataLocation())
                                                .newTransaction(true)
                                                .build())
                                        .then(Mono.empty());
                            } else {
                                return transactionService.saveTransaction(processId, Transaction.builder()
                                                .id(UUID.randomUUID())
                                                .transactionId(processId)
                                                .createdAt(Timestamp.from(Instant.now()))
                                                .entityId(extractEntityIdFromDataLocation(blockchainData.dataLocation()))
                                                .entityType(blockchainData.eventType())
                                                .entityHash("")
                                                .datalocation(blockchainData.dataLocation())
                                                .status(TransactionStatus.PUBLISHED)
                                                .trader(TransactionTrader.PRODUCER)
                                                .newTransaction(true)
                                                .build())
                                        .then(Mono.empty());
                            }
                        })
                .bodyToMono(Void.class)
                .retry(3)
                .onErrorResume(e -> recover(processId, blockchainData));
    }

    @Override
    public Flux<String> getEventsFromRange(String processId, long from, long to) {
        return webClient.get()
                .uri(dltAdapterProperties.paths()
                        .events() + "?startDate=" + from + "&endDate=" + to)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(String.class);
    }

    @Recover
    public Mono<Void> recover(String processId, BlockchainData blockchainData) {
        log.debug("Recovering after 3 retries");
        EventQueuePriority eventQueuePriority = EventQueuePriority.RECOVER_PUBLISH;
        if (!checkIfHashLinkExistInDataLocation(blockchainData.dataLocation())) {
            eventQueuePriority = EventQueuePriority.RECOVER_DELETE;
        } else if (!Objects.equals(blockchainData.previousEntityHash(), "0x0000000000000000000000000000000000000000000000000000000000000000")){
            eventQueuePriority = EventQueuePriority.RECOVER_EDIT;
        }
        return transactionService.saveFailedEventTransaction(processId, FailedEventTransaction.builder()
                        .id(UUID.randomUUID())
                        .transactionId(processId)
                        .createdAt(Timestamp.from(Instant.now()))
                        .entityId(extractEntityIdFromDataLocation(blockchainData.dataLocation()))
                        .entityType(blockchainData.eventType())
                        .datalocation(blockchainData.dataLocation())
                        .organizationId(blockchainData.organizationId())
                        .previousEntityHash(blockchainData.previousEntityHash())
                        .priority(eventQueuePriority)
                        .newTransaction(true)
                        .build())
                .then(Mono.empty());
    }

}
