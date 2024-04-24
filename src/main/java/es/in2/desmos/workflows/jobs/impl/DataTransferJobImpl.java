package es.in2.desmos.workflows.jobs.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exceptions.InvalidConsistencyException;
import es.in2.desmos.domain.exceptions.InvalidIntegrityException;
import es.in2.desmos.domain.exceptions.InvalidSyncResponseException;
import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.domain.services.sync.EntitySyncWebClient;
import es.in2.desmos.domain.utils.ApplicationUtils;
import es.in2.desmos.workflows.jobs.DataTransferJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataTransferJobImpl implements DataTransferJob {
    private final EntitySyncWebClient entitySyncWebClient;
    private final AuditRecordService auditRecordService;
    private final ObjectMapper objectMapper;
    private final BrokerPublisherService brokerPublisherService;

    @Override
    public Mono<Void> syncData(String processId, Mono<DataNegotiationResult> dataNegotiationResult) {
        return dataNegotiationResult.flatMap(result -> {
            log.info("ProcessID: {} - Starting data transfer job", processId);

            Mono<String> issuer = Mono.just(result.issuer());

            Mono<MVEntity4DataNegotiation[]> allEntitiesToRequest = buildAllEntitiesToRequest(
                    Mono.just(result.newEntitiesToSync()),
                    Mono.just(result.existingEntitiesToSync())
            );

            return allEntitiesToRequest.flatMap(entities -> entitySyncWebClient.makeRequest(processId, issuer, allEntitiesToRequest)
                    .flatMap(entitySyncResponse -> {
                        var entitySyncResponseMono = Mono.just(entitySyncResponse);
                        return getEntitiesById(entitySyncResponseMono)
                                .flatMap(entitiesById -> {
                                    var entitiesByIdMono = Mono.just(entitiesById);

                                    Mono<Map<Id, EntityValidationData>> newEntitiesOriginalValidationDataById = getEntitiesOriginalValidationDataById(Mono.just(result.newEntitiesToSync()));
                                    Mono<Map<Id, EntityValidationData>> existingEntitiesOriginalValidationDataById = getEntitiesOriginalValidationDataById(Mono.just(result.existingEntitiesToSync()));

                                    Mono<List<MVEntity4DataNegotiation>> allEntitiesToRequestList = Mono.just(Arrays.asList(entities));

                                    return validateEntities(processId, entitiesByIdMono, newEntitiesOriginalValidationDataById, existingEntitiesOriginalValidationDataById)
                                            .then(buildAndSaveAuditRecordFromDataSync(processId, issuer, entitiesByIdMono, allEntitiesToRequestList, AuditRecordStatus.RETRIEVED))
                                            .then(upsertBatchDataToBroker(processId, entitySyncResponseMono))
                                            .then(buildAndSaveAuditRecordFromDataSync(processId, issuer, entitiesByIdMono, allEntitiesToRequestList, AuditRecordStatus.PUBLISHED));
                                });
                    }));
        });
    }

    private Mono<Void> upsertBatchDataToBroker(String processId, Mono<String> retrievedBrokerEntitiesMono) {
        return retrievedBrokerEntitiesMono.flatMap(retrievedBrokerEntities -> brokerPublisherService.upsertBatchDataToBroker(processId, retrievedBrokerEntities));
    }


    private Mono<Void> buildAndSaveAuditRecordFromDataSync(String processId, Mono<String> issuerMono, Mono<Map<Id, Entity>> rcvdEntitiesByIdMono, Mono<List<MVEntity4DataNegotiation>> mvEntity4DataNegotiationListMono, AuditRecordStatus auditRecordStatus) {
        return rcvdEntitiesByIdMono
                .flatMapIterable(Map::entrySet)
                .flatMap(rcvdEntityById -> {
                    String id = rcvdEntityById.getKey().value();
                    Entity entity = rcvdEntityById.getValue();
                    return mvEntity4DataNegotiationListMono
                            .flatMap(list -> Mono.justOrEmpty(
                                            list.stream()
                                                    .filter(x -> x.id().equals(id))
                                                    .findFirst())
                                    .flatMap(mvEntity4DataNegotiationList -> issuerMono
                                            .flatMap(issuer -> {
                                                String entityValue = entity.value();
                                                return auditRecordService.buildAndSaveAuditRecordFromDataSync(processId, issuer, mvEntity4DataNegotiationList, entityValue, auditRecordStatus);
                                            })));

                })
                .collectList()
                .then();
    }

    private Mono<Map<Id, EntityValidationData>> getEntitiesOriginalValidationDataById(Mono<List<MVEntity4DataNegotiation>> allEntitiesToValidate) {
        return allEntitiesToValidate.map(mvEntity4DataNegotiationsList -> {
            Map<Id, EntityValidationData> entityValidationDataMap = new HashMap<>(mvEntity4DataNegotiationsList.size());
            for (MVEntity4DataNegotiation mvEntity4DataNegotiation : mvEntity4DataNegotiationsList) {
                entityValidationDataMap.put(new Id(mvEntity4DataNegotiation.id()), new EntityValidationData(mvEntity4DataNegotiation.hash(), mvEntity4DataNegotiation.hashlink()));
            }
            return entityValidationDataMap;
        });
    }

    private Mono<MVEntity4DataNegotiation[]> buildAllEntitiesToRequest(Mono<List<MVEntity4DataNegotiation>> newEntitiesToSync, Mono<List<MVEntity4DataNegotiation>> existingEntitiesToSync) {
        return newEntitiesToSync.zipWith(existingEntitiesToSync)
                .flatMap(tuple ->
                        Mono.just(Stream.concat(tuple.getT1().stream(), tuple.getT2().stream()).toArray(MVEntity4DataNegotiation[]::new)));
    }

    private Mono<Map<Id, Entity>> getEntitiesById(Mono<String> entitiesMono) {
        return entitiesMono.flatMap(entities -> {
            try {
                Map<Id, Entity> entitiesById = new HashMap<>();
                JsonNode entitiesJsonNode = objectMapper.readTree(entities);
                if (entitiesJsonNode.isArray()) {
                    entitiesJsonNode.forEach(entityNode -> {
                        String currentEntityId = entityNode.get("id").asText();
                        entitiesById.put(new Id(currentEntityId), new Entity(entityNode.toString()));
                    });
                    return Mono.just(entitiesById);
                } else {
                    return Mono.error(new InvalidSyncResponseException("Invalid EntitySync response."));
                }
            } catch (JsonProcessingException | RuntimeException e) {
                return Mono.error(e);
            }
        });
    }

    private Mono<Void> validateEntities(String processId, Mono<Map<Id, Entity>> entitiesByIdMono, Mono<Map<Id, EntityValidationData>> newEntitiesOriginalValidationDataById, Mono<Map<Id, EntityValidationData>> existingEntitiesOriginalValidationDataById) {
        Mono<Map<Id, EntityValidationData>> allEntitiesOriginalValidationDataById =
                concatTwoEntitiesOriginalValidationDataByIdMaps(
                        newEntitiesOriginalValidationDataById,
                        existingEntitiesOriginalValidationDataById);

        return validateIntegrity(entitiesByIdMono, allEntitiesOriginalValidationDataById)
                .then(validateConsistency(processId, entitiesByIdMono, existingEntitiesOriginalValidationDataById));
    }

    private Mono<Void> validateIntegrity(Mono<Map<Id, Entity>> entitiesByIdMono, Mono<Map<Id, EntityValidationData>> allEntitiesExistingValidationDataById) {
        return entitiesByIdMono
                .flatMapIterable(Map::entrySet)
                .flatMap(entry -> {
                    Mono<String> entity = Mono.just(entry.getValue().value());
                    Id id = entry.getKey();
                    Mono<String> entityRcvdHash = allEntitiesExistingValidationDataById.map(x -> x.get(id).hash());
                    return entity.flatMap(entityValue -> {
                        try {
                            String calculatedHash = ApplicationUtils.calculateSHA256(entityValue);
                            return entityRcvdHash
                                    .flatMap(hashValue -> {
                                        if (calculatedHash.equals(hashValue)) {
                                            return Mono.empty();
                                        } else {
                                            log.error("expected hash: {}\ncurrent hash: {}", calculatedHash, hashValue);
                                            return Mono.error(new InvalidIntegrityException("The hash received at the origin is different from the actual hash of the entity."));
                                        }
                                    });
                        } catch (NoSuchAlgorithmException e) {
                            return Mono.error(e);
                        }
                    });

                })
                .collectList()
                .then();
    }

    private Mono<Void> validateConsistency(String processId, Mono<Map<Id, Entity>> rcvdEntitiesByIdMono, Mono<Map<Id, EntityValidationData>> existingEntitiesValidationDataByIdMono) {
        return rcvdEntitiesByIdMono
                .zipWith(existingEntitiesValidationDataByIdMono)
                .flatMap(tuple -> {
                    Map<Id, Entity> rcvdEntity = tuple.getT1();
                    Map<Id, EntityValidationData> existingEntitiesValidationDataById = tuple.getT2();

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
                                    .findLatestAuditRecordForEntity(processId, id.value())
                                    .flatMap(auditRecord -> {
                                        String entityData = rcvdEntity.get(id).value();
                                        try {
                                            String currentEntityHash = ApplicationUtils.calculateSHA256(entityData);
                                            String existingHashLink = existingEntitiesValidationDataById.get(id).hashLink();
                                            if ((auditRecord.getEntityHashLink() + currentEntityHash).equals(existingHashLink)) {
                                                return Mono.empty();
                                            } else {
                                                return Mono.error(new InvalidConsistencyException("The hashlink received does not correspond to that of the entity."));
                                            }
                                        } catch (NoSuchAlgorithmException e) {
                                            return Mono.error(e);
                                        }
                                    }))
                            .collectList()
                            .then();
                });
    }

    private static Mono<Map<Id, EntityValidationData>> concatTwoEntitiesOriginalValidationDataByIdMaps
            (Mono<Map<Id, EntityValidationData>> newEntitiesOriginalValidationDataById, Mono<Map<Id, EntityValidationData>> existingEntitiesOriginalValidationDataById) {
        return newEntitiesOriginalValidationDataById
                .zipWith(existingEntitiesOriginalValidationDataById)
                .flatMap(tuple -> {
                    Map<Id, EntityValidationData> allEntitiesOriginalDataValidation = new HashMap<>();
                    allEntitiesOriginalDataValidation.putAll(tuple.getT1());
                    allEntitiesOriginalDataValidation.putAll(tuple.getT2());
                    return Mono.just(allEntitiesOriginalDataValidation);
                });
    }
}
