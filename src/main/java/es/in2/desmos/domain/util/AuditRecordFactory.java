package es.in2.desmos.domain.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exception.AuditRecordCreationException;
import es.in2.desmos.domain.model.AuditRecord;
import es.in2.desmos.domain.model.AuditRecordStatus;
import es.in2.desmos.domain.model.AuditRecordTrader;
import es.in2.desmos.domain.service.AuditRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static es.in2.desmos.domain.util.ApplicationUtils.calculateHashLink;
import static es.in2.desmos.domain.util.ApplicationUtils.calculateSHA256;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditRecordFactory {

    private final ObjectMapper objectMapper;
    private final AuditRecordService auditRecordService;


    public Mono<Map<String, Object>> buildAndSaveAuditRecord(String processId, Map<String, Object> dataMap, AuditRecordStatus status, AuditRecordTrader trader) {
        return createAuditRecord(processId, dataMap, status, trader)
                .flatMap(auditRecord -> auditRecordService.saveAuditRecord(processId, auditRecord))
                .thenReturn(dataMap);
    }

    public Mono<AuditRecord> createAuditRecord(String processId, Map<String, Object> dataMap, AuditRecordStatus status,
                                               AuditRecordTrader trader) {
        final String entityId = (String) dataMap.get("id");

        // Convertir dataMap a String fuera del flujo reactivo para manejar excepciones de forma más clara
        final String entityDataAsString;
        try {
            entityDataAsString = objectMapper.writeValueAsString(dataMap);
        } catch (JsonProcessingException e) {
            return Mono.error(new AuditRecordCreationException("Failed to serialize dataMap", e));
        }

        final String entityHash;
        try {
            entityHash = calculateSHA256(entityDataAsString);
        } catch (NoSuchAlgorithmException e) {
            return Mono.error(new AuditRecordCreationException("Failed to calculate entity hash", e));
        }

        return Mono.zip(auditRecordService.findLatestAuditRecordForEntity(processId, entityId),
                        auditRecordService.fetchMostRecentAuditRecord())
                .flatMap(tuple -> {
                    AuditRecord auditRecordFound = tuple.getT1();
                    AuditRecord lastAuditRecordRegistered = tuple.getT2();

                    AuditRecord auditRecord;
                    try {
                        auditRecord = AuditRecord.builder()
                                .id(UUID.randomUUID())
                                .processId(processId)
                                .createdAt(Timestamp.from(Instant.now()))
                                .entityId(entityId)
                                .entityType((String) dataMap.get("type"))
                                .entityHash(entityHash)
                                .entityHashLink(calculateHashLink(auditRecordFound.getEntityHashLink(), entityHash))
                                .dataLocation("") // Asignar adecuadamente
                                .status(status)
                                .trader(trader)
                                .hash("")
                                .hashLink("")
//                                .newTransaction(true)
                                .build();
                    } catch (NoSuchAlgorithmException e) {
                        log.warn(e.getMessage());
                        return Mono.error(new AuditRecordCreationException("Error creating AuditRecord", e));
                    }
                    return calculateAuditRecordHashAndHashLink(auditRecord, lastAuditRecordRegistered.getHashLink());
                })
                .onErrorResume(e -> Mono.error(new AuditRecordCreationException("Error creating AuditRecord", e)));
    }

    public Mono<AuditRecord> calculateAuditRecordHashAndHashLink(AuditRecord auditRecord, String lastAuditRecordHashLink) {
        try {
            // Calculate hash of the digital evidence
            String auditRecordAsString = objectMapper.writeValueAsString(auditRecord);
            String auditRecordHash = calculateSHA256(auditRecordAsString);
            String auditRecordHashLink = calculateHashLink(lastAuditRecordHashLink, auditRecordHash);
            // Set hash and hashLink
            auditRecord.setHash(auditRecordHash);
            auditRecord.setHashLink(auditRecordHashLink);
            return Mono.just(auditRecord);
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            log.warn(e.getMessage());
            return Mono.error(new AuditRecordCreationException("Failed to calculate AuditRecord hash", e));
        }
    }

}
