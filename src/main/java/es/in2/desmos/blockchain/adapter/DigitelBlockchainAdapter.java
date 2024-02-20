package es.in2.desmos.blockchain.adapter;

import es.in2.desmos.api.model.BlockchainEvent;
import es.in2.desmos.api.model.Transaction;
import es.in2.desmos.api.model.TransactionStatus;
import es.in2.desmos.api.model.TransactionTrader;
import es.in2.desmos.api.service.TransactionService;
import es.in2.desmos.blockchain.config.properties.BlockchainAdapterProperties;
import es.in2.desmos.blockchain.model.BlockchainAdapterSubscription;
import es.in2.desmos.blockchain.service.GenericBlockchainAdapterService;
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
public class DigitelBlockchainAdapter implements GenericBlockchainAdapterService {

    private final BlockchainAdapterProperties blockchainAdapterProperties;
    private final TransactionService transactionService;
    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(blockchainAdapterProperties.internalDomain())
                .build();
    }

    @Override
    public Mono<Void> createSubscription(String processId, BlockchainAdapterSubscription blockchainAdapterSubscription) {
        log.info("ProcessId: {} - Creating subscription...", processId);
        return webClient.post()
                .uri(blockchainAdapterProperties.paths()
                        .subscription())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(blockchainAdapterSubscription)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Void> publishEvent(String processId, BlockchainEvent blockchainEvent) {
        return webClient.post()
                .uri(blockchainAdapterProperties.paths()
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
                                                .dataLocation(blockchainEvent.dataLocation())
                                                .entityId(extractEntityIdFromDataLocation(blockchainEvent.dataLocation()))
                                                .entityType(blockchainEvent.eventType())
                                                .hash(extractEntityHashFromDataLocation(blockchainEvent.dataLocation()))
                                                .status(TransactionStatus.DELETED)
                                                .trader(TransactionTrader.PRODUCER)
                                                .newTransaction(true)
                                                .build())
                                        .then(Mono.empty());
                            } else {
                                return transactionService.saveTransaction(processId, Transaction.builder()
                                                .id(UUID.randomUUID())
                                                .transactionId(processId)
                                                .createdAt(Timestamp.from(Instant.now()))
                                                .dataLocation(blockchainEvent.dataLocation())
                                                .entityId(extractEntityIdFromDataLocation(blockchainEvent.dataLocation()))
                                                .entityType(blockchainEvent.eventType())
                                                .hash(extractEntityHashFromDataLocation(blockchainEvent.dataLocation()))
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
                .uri(blockchainAdapterProperties.paths()
                        .events() +"?startDate=" + from + "&endDate=" + to)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(String.class);
    }
}