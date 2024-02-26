package es.in2.desmos.blockchain.adapter;

import es.in2.desmos.api.model.BlockchainEvent;
import es.in2.desmos.api.model.Transaction;
import es.in2.desmos.api.model.TransactionStatus;
import es.in2.desmos.api.model.TransactionTrader;
import es.in2.desmos.api.service.TransactionService;
import es.in2.desmos.blockchain.config.properties.DLTAdapterProperties;
import es.in2.desmos.blockchain.model.DLTAdapterSubscription;
import es.in2.desmos.blockchain.service.GenericDLTAdapterService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static es.in2.desmos.api.util.ApplicationUtils.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DigitelDLTAdapter implements GenericDLTAdapterService {

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
    public Mono<Void> createSubscription(String processId, DLTAdapterSubscription dltAdapterSubscription) {
        log.info("ProcessId: {} - Creating subscription...", processId);
        return webClient.post()
                .uri(dltAdapterProperties.paths()
                        .subscription())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dltAdapterSubscription)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Void> publishEvent(String processId, BlockchainEvent blockchainEvent) {
        return webClient.post()
                .uri(dltAdapterProperties.paths()
                        .publication())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(blockchainEvent)
                .retrieve()
                .onStatus(status -> status != null && status.is2xxSuccessful(),
                        response -> {
                            if (!hasHlParameter(blockchainEvent.dataLocation())) {
                                return transactionService.saveTransaction(processId, Transaction.builder()
                                                .id(UUID.randomUUID())
                                                .transactionId(processId)
                                                .createdAt(Timestamp.from(Instant.now()))
                                                .entityId(extractEntityIdFromDataLocation(blockchainEvent.dataLocation()))
                                                .entityType(blockchainEvent.eventType())
                                                .hash(extractEntityHashFromDataLocation(blockchainEvent.dataLocation()))
                                                .status(TransactionStatus.DELETED)
                                                .trader(TransactionTrader.PRODUCER)
                                                .hashlink(blockchainEvent.dataLocation())
                                                .newTransaction(true)
                                                .hash(extractEntityHashFromDataLocation(blockchainEvent.dataLocation()))
                                                .build())
                                        .then(Mono.empty());
                            } else {
                                return transactionService.saveTransaction(processId, Transaction.builder()
                                                .id(UUID.randomUUID())
                                                .transactionId(processId)
                                                .createdAt(Timestamp.from(Instant.now()))
                                                .entityId(extractEntityIdFromDataLocation(blockchainEvent.dataLocation()))
                                                .entityType(blockchainEvent.eventType())
                                                .hash(extractEntityHashFromDataLocation(blockchainEvent.dataLocation()))
                                                .hashlink(blockchainEvent.dataLocation())
                                                .status(TransactionStatus.PUBLISHED)
                                                .trader(TransactionTrader.PRODUCER)
                                                .newTransaction(true)
                                                .build())
                                        .then(Mono.empty());
                            }
                        })
                .bodyToMono(Void.class);
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

}