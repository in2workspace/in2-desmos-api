package es.in2.desmos.domain.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.configs.ApiConfig;
import es.in2.desmos.configs.BrokerConfig;
import es.in2.desmos.domain.exceptions.HashLinkException;
import es.in2.desmos.domain.models.BlockchainTxPayload;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import static es.in2.desmos.domain.utils.ApplicationUtils.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlockchainDataFactory {

    private final ObjectMapper objectMapper;
    private final ApiConfig apiConfig;
    private final BrokerConfig brokerConfig;

    public Mono<BlockchainTxPayload> buildBlockchainData(String processId, Map<String, Object> dataMap, String previousHash) {
        log.debug("ProcessID: {} - Building blockchain data...", processId);
        try {
            String entityId = dataMap.get("id").toString();
            String entityIdHash = HASH_PREFIX + calculateSHA256(entityId);
            String entityType = (String) dataMap.get("type");
            String entityHash = calculateSHA256(objectMapper.writeValueAsString(dataMap));
            String entityHashlink = ApplicationUtils.calculateHashLink(previousHash, entityHash);
            String dataLocation = brokerConfig.getEntitiesExternalDomain() + "/" + entityId + ApplicationUtils.HASHLINK_PREFIX + entityHashlink;
            String organizationId = HASH_PREFIX + apiConfig.organizationIdHash();
            BlockchainTxPayload blockchainTxPayload = BlockchainTxPayload.builder()
                    .eventType(entityType)
                    .organizationId(organizationId)
                    .entityId(entityIdHash)
                    .previousEntityHash(previousHash)
                    .dataLocation(dataLocation)
                    .metadata(List.of())
                    .build();

            validate(blockchainTxPayload);

            return Mono.just(blockchainTxPayload);
        } catch (ConstraintViolationException e) {
            log.warn("ProcessID: {} - Error validating blockchain transaction payload: {}", processId, e.getMessage());
            return Mono.error(e);
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            log.warn("ProcessID: {} - Error creating blockchain transaction payload: {}", processId, e.getMessage());
            return Mono.error(new HashLinkException("Error creating blockchain transaction payload"));
        }
    }

}