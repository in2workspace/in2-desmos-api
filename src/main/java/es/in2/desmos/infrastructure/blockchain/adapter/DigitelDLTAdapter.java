package es.in2.desmos.infrastructure.blockchain.adapter;

import es.in2.desmos.domain.model.*;
import es.in2.desmos.domain.service.TransactionService;
import es.in2.desmos.infrastructure.configs.properties.DLTAdapterProperties;
import es.in2.desmos.infrastructure.blockchain.model.DLTAdapterSubscription;
import es.in2.desmos.infrastructure.blockchain.service.GenericDLTAdapterService;
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

import static es.in2.desmos.domain.util.ApplicationUtils.*;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableRetry
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

    public Mono<Void> publishEvent(String processId, DLTEvent dltEvent) {
        return webClient.post()
                .uri(dltAdapterProperties.paths()
                        .publication())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dltEvent)
                .retrieve()
                .onStatus(status -> status != null && status.is2xxSuccessful(),
                        response -> {
                            if (!checkIfHashLinkExistInDataLocation(dltEvent.dataLocation())) {
                                return transactionService.saveTransaction(processId, Transaction.builder()
                                                .id(UUID.randomUUID())
                                                .transactionId(processId)
                                                .createdAt(Timestamp.from(Instant.now()))
                                                .entityId(extractEntityIdFromDataLocation(dltEvent.dataLocation()))
                                                .entityType(dltEvent.eventType())
                                                .entityHash(extractHashLinkFromDataLocation(dltEvent.dataLocation()))
                                                .status(TransactionStatus.DELETED)
                                                .trader(TransactionTrader.PRODUCER)
                                                .datalocation(dltEvent.dataLocation())
                                                .newTransaction(true)
                                                .build())
                                        .then(Mono.empty());
                            } else {
                                return transactionService.saveTransaction(processId, Transaction.builder()
                                                .id(UUID.randomUUID())
                                                .transactionId(processId)
                                                .createdAt(Timestamp.from(Instant.now()))
                                                .entityId(extractEntityIdFromDataLocation(dltEvent.dataLocation()))
                                                .entityType(dltEvent.eventType())
                                                .entityHash("")
                                                .datalocation(dltEvent.dataLocation())
                                                .status(TransactionStatus.PUBLISHED)
                                                .trader(TransactionTrader.PRODUCER)
                                                .newTransaction(true)
                                                .build())
                                        .then(Mono.empty());
                            }
                        })
                .bodyToMono(Void.class)
                .retry(3)
                .onErrorResume(e -> recover(processId, dltEvent));
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
    public Mono<Void> recover(String processId, DLTEvent dltEvent) {
        log.debug("Recovering after 3 retries");
        EventQueuePriority eventQueuePriority = EventQueuePriority.RECOVER_PUBLISH;
        if (!checkIfHashLinkExistInDataLocation(dltEvent.dataLocation())) {
            eventQueuePriority = EventQueuePriority.RECOVER_DELETE;
        } else if (!Objects.equals(dltEvent.previousEntityHash(), "0x0000000000000000000000000000000000000000000000000000000000000000")){
            eventQueuePriority = EventQueuePriority.RECOVER_EDIT;
        }
        return transactionService.saveFailedEventTransaction(processId, FailedEventTransaction.builder()
                        .id(UUID.randomUUID())
                        .transactionId(processId)
                        .createdAt(Timestamp.from(Instant.now()))
                        .entityId(extractEntityIdFromDataLocation(dltEvent.dataLocation()))
                        .entityType(dltEvent.eventType())
                        .datalocation(dltEvent.dataLocation())
                        .organizationId(dltEvent.organizationId())
                        .previousEntityHash(dltEvent.previousEntityHash())
                        .priority(eventQueuePriority)
                        .newTransaction(true)
                        .build())
                .then(Mono.empty());
    }

}
