package es.in2.desmos.blockchain.adapter;

import es.in2.desmos.api.model.BlockchainEvent;
import es.in2.desmos.api.model.Transaction;
import es.in2.desmos.api.model.TransactionStatus;
import es.in2.desmos.api.model.TransactionTrader;
import es.in2.desmos.api.service.TransactionService;
import es.in2.desmos.blockchain.config.properties.BlockchainAdapterProperties;
import es.in2.desmos.blockchain.model.BlockchainNode;
import es.in2.desmos.blockchain.model.BlockchainAdapterSubscription;
import es.in2.desmos.blockchain.service.GenericBlockchainAdapterService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static es.in2.desmos.api.util.ApplicationUtils.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DigitelBlockchainAdapter implements GenericBlockchainAdapterService {

    private static final String DOME_SESSION_COOKIE = "sessionCookieDOME";
    private final BlockchainAdapterProperties blockchainAdapterProperties;
    private final TransactionService transactionService;
    private WebClient webClient;
    private ResponseCookie sessionCookie;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(blockchainAdapterProperties.internalDomain())
                .filter((request, next) -> next.exchange(request)
                        .doOnNext(response -> {
                            List<ResponseCookie> cookies = response.cookies().get(DOME_SESSION_COOKIE);
                            if (cookies != null && !cookies.isEmpty()) {
                                sessionCookie = cookies.get(0);
                            }
                        }))
                .build();
    }

    @Override
    public Mono<String> setNodeConnection(String processId, BlockchainNode blockchainNode) {
        return webClient.post()
                .uri(blockchainAdapterProperties.paths().nodeConfiguration())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(blockchainNode)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public Mono<Void> createSubscription(String processId, BlockchainAdapterSubscription blockchainAdapterSubscription) {
        log.info("ProcessId: {} - Creating subscription...", processId);
        log.debug("Session Cookie: {}", sessionCookie);
        return webClient.post()
                .uri(blockchainAdapterProperties.paths().subscription())
                .cookie(DOME_SESSION_COOKIE, sessionCookie.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(blockchainAdapterSubscription)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Void> publishEvent(String processId, BlockchainEvent blockchainEvent) {
        return webClient.post()
                .uri(blockchainAdapterProperties.paths().publication())
                .cookie(DOME_SESSION_COOKIE, sessionCookie.getValue())
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
                                                .entityHash(extractEntityHashFromDataLocation(blockchainEvent.dataLocation()))
                                                .status(TransactionStatus.DELETED)
                                                .trader(TransactionTrader.PRODUCER)
                                                .hash("")
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
                                                .entityHash(extractEntityIdFromDataLocation(blockchainEvent.dataLocation()))
                                                .status(TransactionStatus.PUBLISHED)
                                                .trader(TransactionTrader.PRODUCER)
                                                .hash("")
                                                .build())
                                        .then(Mono.empty());
                            }
                        })
                .bodyToMono(Void.class);
    }
}
