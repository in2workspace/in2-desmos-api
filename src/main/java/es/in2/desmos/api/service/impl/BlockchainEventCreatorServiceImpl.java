package es.in2.desmos.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.api.config.ApplicationConfig;
import es.in2.desmos.api.exception.HashCreationException;
import es.in2.desmos.api.exception.HashLinkException;
import es.in2.desmos.api.model.BlockchainEvent;
import es.in2.desmos.api.model.Transaction;
import es.in2.desmos.api.model.TransactionStatus;
import es.in2.desmos.api.model.TransactionTrader;
import es.in2.desmos.api.service.BlockchainEventCreatorService;
import es.in2.desmos.api.service.TransactionService;
import es.in2.desmos.broker.config.properties.BrokerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static es.in2.desmos.api.util.ApplicationUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockchainEventCreatorServiceImpl implements BlockchainEventCreatorService {

    private static final String ID_KEY_ATTRIBUTE = "id";
    private final BrokerProperties brokerProperties;
    private final TransactionService transactionService;
    private final ApplicationConfig applicationConfig;
    private final ObjectMapper objectMapper;

    private static String generateEntityIdHashFromDataLocation(String dataLocation) {
        try {
            URI uri = new URI(dataLocation);
            String path = uri.getPath();
            String entityId = path.substring(path.lastIndexOf('/') + 1);
            return calculateSHA256Hash(entityId);
        } catch (NoSuchAlgorithmException | URISyntaxException ex) {
            throw new HashCreationException("Error while calculating hash to create onChainEvent");
        }
    }

    private Mono<String> getPreviousEntityHashFromTransaction(String processId, String entityId) {
        return transactionService.getLastProducerTransactionByEntityId(processId, entityId)
                .flatMap(transaction -> transaction != null ? Mono.fromCallable(transaction::getEntityHash) : Mono.empty())
                .switchIfEmpty(Mono.just("0x0000000000000000000000000000000000000000000000000000000000000000"));
    }

    @Override
    public Mono<BlockchainEvent> createBlockchainEvent(String processId, Map<String, Object> dataMap) {
        return getPreviousEntityHashFromTransaction(processId, dataMap.get(ID_KEY_ATTRIBUTE).toString())
                .flatMap(previousHash -> {
                    // Build DataLocation parameter
                    String brokerEntityLocation = brokerProperties.internalDomain() + brokerProperties.paths().entities();
                    String dataLocation = brokerEntityLocation + "/" + dataMap.get(ID_KEY_ATTRIBUTE).toString();
                    if (!dataMap.containsKey("deletedAt")) {
                        try {
                            String dataMapAsString = objectMapper.writeValueAsString(dataMap);
                            String entityHashed = calculateSHA256Hash(dataMapAsString);
                            entityHashed = previousHash.equals("0x0000000000000000000000000000000000000000000000000000000000000000") ?
                                    entityHashed :
                                    calculateIntertwinedHash(entityHashed, previousHash);
                            previousHash = previousHash.startsWith(HASH_PREFIX) ? previousHash : HASH_PREFIX + previousHash;
                            dataLocation =
                                    brokerEntityLocation + "/" + dataMap.get(ID_KEY_ATTRIBUTE).toString() + HASHLINK_PREFIX + entityHashed;
                        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
                            log.error("ProcessID: {} - Error creating blockchain event: {}", processId, e.getMessage());
                            return Mono.error(new HashLinkException("Error creating blockchain event", e.getCause()));
                        }
                    }
                    // Build BlockchainEvent
                    BlockchainEvent blockchainEvent = BlockchainEvent.builder()
                            .eventType((String) dataMap.get("type"))
                            .organizationId(HASH_PREFIX + applicationConfig.organizationIdHash())
                            .entityId(HASH_PREFIX + generateEntityIdHashFromDataLocation(dataLocation))
                            .previousEntityHash(previousHash)
                            .dataLocation(dataLocation)
                            .metadata(List.of())
                            .build();
                    log.debug("ProcessID: {} - BlockchainEvent created: {}", processId, blockchainEvent.toString());
                    return transactionService.saveTransaction(processId, Transaction.builder()
                                    .id(UUID.randomUUID())
                                    .transactionId(processId)
                                    .createdAt(Timestamp.from(Instant.now()))
                                    .entityId(extractEntityIdFromDataLocation(blockchainEvent.dataLocation()))
                                    .entityType(blockchainEvent.eventType())
                                    .entityHash(extractEntityHashFromDataLocation(blockchainEvent.dataLocation()))
                                    .status(TransactionStatus.CREATED)
                                    .trader(TransactionTrader.PRODUCER)
                                    .datalocation(blockchainEvent.dataLocation())
                                    .newTransaction(true)
                                    .build())
                            .thenReturn(blockchainEvent);
                });
    }

}