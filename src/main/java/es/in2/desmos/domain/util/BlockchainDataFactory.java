package es.in2.desmos.domain.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.configs.ApiConfig;
import es.in2.desmos.configs.BrokerConfig;
import es.in2.desmos.domain.exception.HashLinkException;
import es.in2.desmos.domain.model.AuditRecord;
import es.in2.desmos.domain.model.AuditRecordStatus;
import es.in2.desmos.domain.model.AuditRecordTrader;
import es.in2.desmos.domain.model.BlockchainData;
import es.in2.desmos.domain.service.AuditRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static es.in2.desmos.domain.util.ApplicationUtils.HASH_PREFIX;
import static es.in2.desmos.domain.util.ApplicationUtils.calculateSHA256;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlockchainDataFactory {

    private final ObjectMapper objectMapper;
    private final ApiConfig apiConfig;
    private final BrokerConfig brokerConfig;

    public Mono<BlockchainData> buildBlockchainData(String processId, Map<String, Object> dataMap, String previousHash) {
        log.debug("ProcessID: {} - Building blockchain data...", processId);
        try {
            String entityId = dataMap.get("id").toString();
            String entityIdHash = HASH_PREFIX + calculateSHA256(entityId);
            String entityType = (String) dataMap.get("type");
            String entityHash = calculateSHA256(objectMapper.writeValueAsString(dataMap));
            String entityHashlink = ApplicationUtils.calculateHashLink(previousHash, entityHash);
            String dataLocation = brokerConfig.getEntitiesExternalDomain() + "/" + entityId + ApplicationUtils.HASHLINK_PREFIX + entityHashlink;
            String organizationId = HASH_PREFIX + apiConfig.organizationIdHash();
            return Mono.just(BlockchainData.builder()
                    .eventType(entityType)
                    .organizationId(organizationId)
                    .entityId(entityIdHash)
                    .previousEntityHash(previousHash)
                    .dataLocation(dataLocation)
                    .metadata(List.of())
                    .build());
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            log.error("ProcessID: {} - Error creating blockchain data: {}", processId, e.getMessage());
            return Mono.error(new HashLinkException("Error creating blockchain data", e.getCause()));
        }
    }


//    public Mono<BlockchainData> buildDLTEvent(String processId, Map<String, Object> dataMap, String previousHash) {
//        // Build DataLocation parameter
//        try {
//            String entityId = dataMap.get("id").toString();
//            String entityIdHash = ApplicationUtils.HASH_PREFIX + ApplicationUtils.calculateSHA256(entityId);
//            String entityType = (String) dataMap.get("type");
//            String entityHash = ApplicationUtils.calculateSHA256(objectMapper.writeValueAsString(dataMap));
//            String entityHashlink = ApplicationUtils.calculateHashLink(previousHash, entityHash);
//            String dataLocation = brokerConfig.getEntitiesExternalDomain() + "/" + entityId + ApplicationUtils.HASHLINK_PREFIX + entityHashlink;
//            return Mono.just(BlockchainData.builder()
//                    .eventType(entityType)
//                            .organizationId(ApplicationUtils.HASH_PREFIX + apiConfig.organizationIdHash())
//                    .entityId(entityIdHash)
//                    .previousEntityHash(previousHash)
//                    .dataLocation(dataLocation)
//                    .metadata(List.of())
//                    .build())
//                    .flatMap(dltEvent -> auditRecordService.saveAuditRecord(processId, AuditRecord.builder()
//                                    .id(UUID.randomUUID())
//                                    .processId(processId)
//                                    .createdAt(Timestamp.from(Instant.now()))
//                                    .entityId(entityIdHash)
//                                    .entityType(entityType)
//                                    .entityHash(entityHash)
//                                    .entityHashLink(entityHashlink)
//                                    .dataLocation(dataLocation)
//                                    .status(AuditRecordStatus.CREATED)
//                                    .trader(AuditRecordTrader.PRODUCER)
//                                    // TODO: How to calculate hash and hashlink?
//                                    .hash("")
//                                    .hashLink("")
//                                    .build())
//                            .thenReturn(dltEvent)
//                    );
//        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
//            log.error("ProcessID: {} - Error creating blockchain event: {}", processId, e.getMessage());
//            return Mono.error(new HashLinkException("Error creating blockchain event", e.getCause()));
//        }
//    }

}
