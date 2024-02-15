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

    private final BrokerProperties brokerProperties;
    private final TransactionService transactionService;
    private final ApplicationConfig applicationConfig;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<BlockchainEvent> createBlockchainEvent(String processId, Map<String, Object> dataMap) {
        // Build DataLocation parameter (Hashlink)
        String brokerEntityLocation = brokerProperties.internalDomain() + brokerProperties.paths().entities();
        String dataLocation = brokerEntityLocation + "/" + dataMap.get("id").toString();
        if (!dataMap.containsKey("deletedAt")) {
            try {
                String dataMapAsString = objectMapper.writeValueAsString(dataMap);
                String entityHashed = calculateSHA256Hash(dataMapAsString);
                dataLocation = brokerEntityLocation + "/" + dataMap.get("id").toString() + HASHLINK_PREFIX + entityHashed;
            } catch (JsonProcessingException | NoSuchAlgorithmException e) {
                log.error("ProcessID: {} - Error creating blockchain event: {}", processId, e.getMessage());
                throw new HashLinkException("Error creating blockchain event", e.getCause());
            }
        }
        BlockchainEvent blockchainEvent = BlockchainEvent.builder()
                .eventType((String) dataMap.get("type"))
                .organizationId(applicationConfig.organizationIdHash())
                .entityId(generateEntityIdHashFromDataLocation(dataLocation))
                .previousEntityHash("0x0000000000000000000000000000000000000000000000000000000000000000")
                .dataLocation(dataLocation)
                .metadata(List.of())
                .build();
        // Build BlockchainEvent
        return transactionService.saveTransaction(processId, Transaction.builder()
                .id(UUID.randomUUID())
                .transactionId(processId)
                .createdAt(Timestamp.from(Instant.now()))
                .dataLocation(blockchainEvent.dataLocation())
                .entityId(blockchainEvent.entityId())
                .entityType(blockchainEvent.eventType())
                .entityHash(extractEntityHashFromDataLocation(blockchainEvent.dataLocation()))
                .status(TransactionStatus.CREATED)
                .trader(TransactionTrader.PRODUCER)
                .hash("")
                        .newTransaction(true)
                .build())
                .thenReturn(blockchainEvent);
    }

    private static String generateEntityIdHashFromDataLocation(String datalocation) {
        try {
            URI uri = new URI(datalocation);
            String path = uri.getPath();
            String entityId = path.substring(path.lastIndexOf('/') + 1);
            return calculateSHA256Hash(entityId);
        } catch (NoSuchAlgorithmException | URISyntaxException ex) {
            throw new HashCreationException("Error while calculating hash to create onChainEvent");
        }
    }

}
