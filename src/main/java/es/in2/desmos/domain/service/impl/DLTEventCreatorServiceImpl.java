package es.in2.desmos.domain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.model.*;
import es.in2.desmos.domain.service.AuditRecordService;
import es.in2.desmos.infrastructure.configs.ApiConfig;
import es.in2.desmos.domain.exception.HashCreationException;
import es.in2.desmos.domain.exception.HashLinkException;
import es.in2.desmos.domain.service.DLTEventCreatorService;
import es.in2.desmos.domain.service.TransactionService;
import es.in2.desmos.infrastructure.configs.BrokerConfig;
import es.in2.desmos.infrastructure.configs.properties.BrokerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static es.in2.desmos.domain.util.ApplicationUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DLTEventCreatorServiceImpl implements DLTEventCreatorService {

    private final ObjectMapper objectMapper;
    private final ApiConfig apiConfig;
    private final BrokerConfig brokerConfig;
    private final AuditRecordService auditRecordService;

    @Override
    public Mono<DLTEvent> buildDLTEvent(String processId, Map<String, Object> dataMap, String previousHash) {
        // Build DataLocation parameter
        try {
            String entityId = dataMap.get("id").toString();
            String entityIdHash = HASH_PREFIX + calculateSHA256(entityId);
            String entityType = (String) dataMap.get("type");
            String entityHash = calculateSHA256(objectMapper.writeValueAsString(dataMap));
            String entityHashlink = calculateHashLink(previousHash, entityHash);
            String dataLocation = brokerConfig.getEntitiesExternalDomain() + "/" + entityId  + HASHLINK_PREFIX + entityHashlink;
            return Mono.just(DLTEvent.builder()
                    .eventType(entityType)
                    .organizationId(HASH_PREFIX + apiConfig.organizationIdHash())
                    .entityId(entityIdHash)
                    .previousEntityHash(previousHash)
                    .dataLocation(dataLocation)
                    .metadata(List.of())
                    .build())
                    .flatMap(dltEvent -> auditRecordService.saveAuditRecord(processId, AuditRecord.builder()
                                    .id(UUID.randomUUID())
                                    .processId(processId)
                                    .createdAt(Timestamp.from(Instant.now()))
                                    .entityId(entityIdHash)
                                    .entityType(entityType)
                                    .entityHash(entityHash)
                                    .entityHashLink(entityHashlink)
                                    .dataLocation(dataLocation)
                                    .status(AuditRecordStatus.CREATED)
                                    .trader(AuditRecordTrader.PRODUCER)
                                    // TODO: How to calculate hash and hashlink?
                                    .hash("")
                                    .hashLink("")
                                    .build())
                            .thenReturn(dltEvent)
                    );
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            log.error("ProcessID: {} - Error creating blockchain event: {}", processId, e.getMessage());
            return Mono.error(new HashLinkException("Error creating blockchain event", e.getCause()));
        }
    }

}