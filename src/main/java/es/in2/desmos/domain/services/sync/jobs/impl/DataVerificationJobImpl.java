package es.in2.desmos.domain.services.sync.jobs.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exceptions.InvalidConsistencyException;
import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.domain.services.sync.jobs.DataVerificationJob;
import es.in2.desmos.domain.utils.ApplicationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

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

                                            if ((auditRecord.getEntityHashLink() + currentEntityHash).equals(existingHashLink)) {
                                                return Mono.empty();
                                            } else {
                                                return Mono.error(new InvalidConsistencyException("The hashlink received does not correspond to that of the entity."));
                                            }
                                        });
                                    }))
                            .collectList()
                            .then();
                });
    }

    private Mono<Void> buildAndSaveAuditRecordFromDataSync(String processId, Mono<String> issuerMono, Mono<Map<Id, Entity>> rcvdEntitiesByIdMono, Mono<List<MVEntity4DataNegotiation>> mvEntity4DataNegotiationListMono, AuditRecordStatus auditRecordStatus) {
        return rcvdEntitiesByIdMono
                .flatMapIterable(Map::keySet)
                .flatMap(rcvdEntityById -> {
                    String id = rcvdEntityById.id();
                    return mvEntity4DataNegotiationListMono
                            .flatMap(list -> Mono.justOrEmpty(
                                            list.stream()
                                                    .filter(x -> x.id().equals(id))
                                                    .findFirst())
                                    .flatMap(mvEntity4DataNegotiationList -> issuerMono
                                            .flatMap(issuer -> auditRecordService.buildAndSaveAuditRecordFromDataSync(processId, issuer, mvEntity4DataNegotiationList, auditRecordStatus))));

                })
                .collectList()
                .then();
    }

    private Mono<Void> batchUpsertEntitiesToContextBroker(String processId, Mono<String> retrievedBrokerEntitiesMono) {
        return retrievedBrokerEntitiesMono.flatMap(retrievedBrokerEntities -> brokerPublisherService.batchUpsertEntitiesToContextBroker(processId, retrievedBrokerEntities));
    }

    private Mono<String> calculateHash(Mono<String> retrievedBrokerEntityMono) {
        Mono<String> sortedAttributesBrokerEntityMono = sortAttributesAlphabetically(retrievedBrokerEntityMono);
        return sortedAttributesBrokerEntityMono.flatMap(sortedAttributesBrokerEntity -> {
            try {
                return Mono.just(ApplicationUtils.calculateSHA256(sortedAttributesBrokerEntity));
            } catch (NoSuchAlgorithmException e) {
                return Mono.error(e);
            }
        });
    }

    private Mono<String> sortAttributesAlphabetically(Mono<String> retrievedBrokerEntityMono) {
        return retrievedBrokerEntityMono.flatMap(retrievedBrokerEntity -> {
            try {
                JsonNode retrievedBrokerEntityJsonNode = objectMapper.readTree(retrievedBrokerEntity);
                return Mono.just(objectMapper.writeValueAsString(retrievedBrokerEntityJsonNode));
            } catch (JsonProcessingException e) {
                return Mono.error(e);
            }
        });
    }
}