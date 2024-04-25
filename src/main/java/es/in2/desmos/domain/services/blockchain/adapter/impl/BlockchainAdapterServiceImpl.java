package es.in2.desmos.domain.services.blockchain.adapter.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.configs.properties.DLTAdapterProperties;
import es.in2.desmos.domain.exceptions.JsonReadingException;
import es.in2.desmos.domain.exceptions.RequestErrorException;
import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.services.api.RecoverRepositoryService;
import es.in2.desmos.domain.services.blockchain.adapter.BlockchainAdapterService;
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

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableRetry
public class BlockchainAdapterServiceImpl implements BlockchainAdapterService {

    private final DLTAdapterProperties dltAdapterProperties;
    private final RecoverRepositoryService recoverRepositoryService;
    private final ObjectMapper objectMapper;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(dltAdapterProperties.externalDomain())
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

    public Mono<Void> postTxPayload(String processId, BlockchainTxPayload blockchainTxPayload) {
        return webClient.post()
                .uri(dltAdapterProperties.paths().publication())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(blockchainTxPayload)
                .retrieve()
                .onStatus(status -> status != null && status.is2xxSuccessful(),
                        response -> Mono.empty())
                .bodyToMono(Void.class)
                .retry(3)
                .onErrorResume(e -> recover(processId, blockchainTxPayload)
                        .then(Mono.defer(() -> Mono.error(new RequestErrorException("Error posting transaction payload to DLT Adapter")))));
    }

    @Deprecated(since = "0.5.0", forRemoval = true)
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
    public Mono<Void> recover(String processId, BlockchainTxPayload blockchainTxPayload) {
        log.debug("ProcessId: {} - Recovering failed transaction", processId);
        try {
            return recoverRepositoryService.saveBlockchainTxPayloadRecover(processId, BlockchainTxPayloadRecover.builder()
                    .id(UUID.randomUUID())
                    .entityId(blockchainTxPayload.entityId())
                    .eventType(blockchainTxPayload.eventType())
                    .organizationId(blockchainTxPayload.organizationId())
                    .previousEntityHash(blockchainTxPayload.previousEntityHash())
                    .dataLocation(blockchainTxPayload.dataLocation())
                    .relevantMetadata(objectMapper.writeValueAsString(blockchainTxPayload.metadata()))
                    .processId(processId)
                    .eventQueuePriority(EventQueuePriority.CRITICAL.toString())
                    .newTransaction(true)
                    .build());
        } catch (JsonProcessingException e) {
            throw new JsonReadingException("Error serializing BlockchainTxPayload");
        }
    }

}
