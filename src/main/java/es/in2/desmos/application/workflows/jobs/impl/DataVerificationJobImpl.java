package es.in2.desmos.application.workflows.jobs.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.application.workflows.jobs.DataVerificationJob;
import es.in2.desmos.domain.exceptions.InvalidConsistencyException;
import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.domain.utils.ApplicationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataVerificationJobImpl implements DataVerificationJob {
    private final AuditRecordService auditRecordService;
    private final BrokerPublisherService brokerPublisherService;
    private final ObjectMapper objectMapper;

    public Mono<Void> verifyData(String processId, Mono<String> issuer, Mono<Map<Id, Entity>> entitiesByIdMono, Mono<List<MVEntity4DataNegotiation>> allMVEntity4DataNegotiation, Mono<String> entitySyncResponseMono, Mono<Map<Id, HashAndHashLink>> existingEntitiesOriginalValidationDataById) {
        log.info("ProcessID: {} - Starting Data Verification Job", processId);

        return validateConsistency(processId, entitiesByIdMono, existingEntitiesOriginalValidationDataById)
                .then(buildAndSaveAuditRecordFromDataSync(processId, issuer, entitiesByIdMono, allMVEntity4DataNegotiation, AuditRecordStatus.RETRIEVED))
                .then(batchUpsertEntitiesToContextBroker(processId, entitySyncResponseMono))
                .then(buildAndSaveAuditRecordFromDataSync(processId, issuer, entitiesByIdMono, allMVEntity4DataNegotiation, AuditRecordStatus.PUBLISHED));
    }

    private Mono<Void> validateConsistency(String processId, Mono<Map<Id, Entity>> rcvdEntitiesByIdMono, Mono<Map<Id, HashAndHashLink>> existingEntitiesValidationDataByIdMono) {
        return rcvdEntitiesByIdMono
                .zipWith(existingEntitiesValidationDataByIdMono)
                .flatMap(tuple -> {
                    Map<Id, Entity> rcvdEntity = tuple.getT1();
                    Map<Id, HashAndHashLink> existingEntitiesValidationDataById = tuple.getT2();

                    List<Id> commonEntities = rcvdEntity.keySet().stream()
                            .filter(recvdId -> existingEntitiesValidationDataById
                                    .keySet()
                                    .stream()
                                    .anyMatch(existingId -> existingId.equals(recvdId)))
                            .toList();
                    Mono<List<Id>> idMonoList = Mono.just(commonEntities);
                    return idMonoList
                            .flatMapIterable(list -> list)
                            .flatMap(id -> auditRecordService
                                    .findLatestAuditRecordForEntity(processId, id.id())
                                    .flatMap(auditRecord -> {
                                        String entityData = rcvdEntity.get(id).value();
                                        Mono<String> entityDataMono = Mono.just(entityData);
                                        Mono<String> currentEntityHashMono = calculateHash(entityDataMono);
                                        return currentEntityHashMono.flatMap(currentEntityHash -> {
                                            String existingHashLink = existingEntitiesValidationDataById.get(id).hashLink();

                                            try {
                                                String calculatedHashLink = ApplicationUtils.calculateHashLink(auditRecord.getEntityHashLink(), currentEntityHash);

                                                if (calculatedHashLink.equals(existingHashLink)) {
                                                    return Mono.empty();
                                                } else {
                                                    log.debug("ProcessID: {} - Starting Data Verification Job\nId: {}\nEntity: {}\n OldHashlink: {}\n New Hashlink: {}\n Calculated hashlink: {}\n Expected hashlink: {}", processId, id.id(), entityData, auditRecord.getEntityHashLink(), currentEntityHash, calculatedHashLink, existingHashLink);
                                                    return Mono.error(new InvalidConsistencyException("The hashlink received does not correspond to that of the entity."));
                                                }
                                            } catch (NoSuchAlgorithmException | JsonProcessingException e) {
                                                log.warn("ProcessID: {} - Error Calculating hashlink: {}", processId, e.getMessage());
                                                log.debug("ProcessID: {} - Error Calculating hashlink:\nId: {}\nEntity: {}\n OldHashlink: {}\n New Hashlink: {}\n Expected hashlink: {}", processId, id.id(), entityData, auditRecord.getEntityHashLink(), currentEntityHash, existingHashLink);
                                                return Mono.error(e);
                                            }
                                        });
                                    }))
                            .collectList()
                            .then();
                });
    }

    private Mono<Void> buildAndSaveAuditRecordFromDataSync(String processId, Mono<String> issuerMono, Mono<Map<Id, Entity>> rcvdEntitiesByIdMono, Mono<List<MVEntity4DataNegotiation>> mvEntity4DataNegotiationListMono, AuditRecordStatus auditRecordStatus) {
        return rcvdEntitiesByIdMono
                .flatMapIterable(Map::entrySet)
                .flatMap(rcvdEntityById -> {
                    String id = rcvdEntityById.getKey().id();

                    return mvEntity4DataNegotiationListMono
                            .flatMap(list -> {
                                Optional<MVEntity4DataNegotiation> mvEntity4DataNegotiation = list.stream()
                                        .filter(x -> x.id().equals(id))
                                        .findFirst();
                                return mvEntity4DataNegotiation.map(entity4DataNegotiation -> issuerMono
                                                .flatMap(issuer -> auditRecordService
                                                        .buildAndSaveAuditRecordFromDataSync(processId, issuer, entity4DataNegotiation, auditRecordStatus)))
                                        .orElseGet(() -> issuerMono
                                                .flatMap(issuer -> getMVEntity4DataNegotiationForNewSubEntity(processId, Mono.just(rcvdEntityById), Mono.just(id))
                                                        .flatMap(newMVEntity4DataNegotiation -> auditRecordService
                                                                .buildAndSaveAuditRecordFromDataSync(processId, issuer, newMVEntity4DataNegotiation, auditRecordStatus))));
                            });

                })
                .collectList()
                .then();
    }

    private Mono<MVEntity4DataNegotiation> getMVEntity4DataNegotiationForNewSubEntity(String processId, Mono<Map.Entry<Id, Entity>> rcvdEntityByIdMono, Mono<String> idMono) {
        return rcvdEntityByIdMono.flatMap(rcvdEntityById -> {
            String entity = rcvdEntityById.getValue().value();
            try {
                JsonNode entityNode = objectMapper.readTree(entity);

                String type = entityNode.get("type").asText();
                String lastUpdate = entityNode.get("lastUpdate").get("value").asText();
                String version = entityNode.get("version").get("value").asText();

                Mono<String> calculatedHashMono = calculateHash(Mono.just(entity));
                Mono<String> hashLinkMono = getHashLinkForNewSubEntity(processId, calculatedHashMono, idMono);
                return idMono
                        .zipWith(hashLinkMono)
                        .flatMap(tuple -> {
                            String id = tuple.getT1();
                            String hashLink = tuple.getT2();

                            return calculatedHashMono.map(hash ->
                                    new MVEntity4DataNegotiation(id, type, version, lastUpdate, hash, hashLink));
                        });
            } catch (JsonProcessingException e) {
                return Mono.error(e);
            }
        });
    }

    private Mono<String> getHashLinkForNewSubEntity(String processId, Mono<String> hashMono, Mono<String> idMono) {
        return idMono.flatMap(entityId ->
                auditRecordService.findLatestAuditRecordForEntity(processId, entityId)
                        .filter(auditRecord -> auditRecord.getEntityId().equals(entityId))
                        .map(AuditRecord::getEntityHashLink)
                        .switchIfEmpty(hashMono)
        );
    }

    private Mono<Void> batchUpsertEntitiesToContextBroker(String processId, Mono<String> retrievedBrokerEntitiesMono) {
        return retrievedBrokerEntitiesMono.flatMap(retrievedBrokerEntities -> brokerPublisherService.batchUpsertEntitiesToContextBroker(processId, retrievedBrokerEntities));
    }

    private Mono<String> calculateHash(Mono<String> retrievedBrokerEntityMono) {
        return retrievedBrokerEntityMono.flatMap(sortedAttributesBrokerEntity -> {
            try {
                return Mono.just(ApplicationUtils.calculateSHA256(sortedAttributesBrokerEntity));
            } catch (NoSuchAlgorithmException | JsonProcessingException e) {
                return Mono.error(e);
            }
        });
    }
}