package es.in2.desmos.domain.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.configs.ApiConfig;
import es.in2.desmos.configs.BrokerConfig;
import es.in2.desmos.domain.exceptions.HashLinkException;
import es.in2.desmos.domain.models.BlockchainTxPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import static es.in2.desmos.domain.utils.ApplicationConstants.HASH_LINK_PREFIX;
import static es.in2.desmos.domain.utils.ApplicationConstants.HASH_PREFIX;
import static es.in2.desmos.domain.utils.ApplicationUtils.calculateSHA256;
import static es.in2.desmos.domain.utils.ApplicationUtils.getEnvironmentMetadata;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlockchainTxPayloadFactory {

    private final ObjectMapper objectMapper;
    private final ApiConfig apiConfig;
    private final BrokerConfig brokerConfig;

    public Mono<BlockchainTxPayload> buildBlockchainTxPayload(String processId, Map<String, Object> dataMap, String previousHash) {
        log.debug("ProcessID: {} - Building blockchain data...", processId);
        try {
            String entityId = dataMap.get("id").toString();
            String entityIdHash = HASH_PREFIX + calculateSHA256(entityId);
            String entityType = (String) dataMap.get("type");
            String entityHash = calculateSHA256(objectMapper.writeValueAsString(dataMap));
            String entityHashLink = entityHash.equals(previousHash) ? previousHash : ApplicationUtils.calculateHashLink(previousHash, entityHash);
            String dataLocation = brokerConfig.getEntitiesExternalDomain() + "/" + entityId + HASH_LINK_PREFIX + entityHashLink;
            String organizationId = HASH_PREFIX + apiConfig.organizationIdHash();
            String previousEntityHash = HASH_PREFIX + previousHash;
            List<String> metadata = List.of(getEnvironmentMetadata(apiConfig.getCurrentEnvironment()));
            return Mono.just(BlockchainTxPayload.builder()
                    .eventType(entityType)
                    .organizationId(organizationId)
                    .entityId(entityIdHash)
                    .previousEntityHash(previousEntityHash)
                    .dataLocation(dataLocation)
                    .metadata(metadata)
                    .build());
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            log.warn("ProcessID: {} - Error creating blockchain transaction payload: {}", processId, e.getMessage());
            return Mono.error(new HashLinkException("Error creating blockchain transaction payload"));
        }
    }

    public Mono<String> calculatePreviousHashIfEmpty(String processId, Map<String, Object> dataMap) {
        try {
            return Mono.just(calculateSHA256(objectMapper.writeValueAsString(dataMap)));
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            log.warn("ProcessID: {} - Error creating previous hash from notification data: {}", processId, e.getMessage());
            return Mono.error(new HashLinkException("Error creating previous hash value from notification data"));
        }
    }

}