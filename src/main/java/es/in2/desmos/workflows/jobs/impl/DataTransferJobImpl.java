package es.in2.desmos.workflows.jobs.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exceptions.InvalidConsistencyException;
import es.in2.desmos.domain.exceptions.InvalidIntegrityException;
import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.services.api.AuditRecordService;
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

    @Override
    public Mono<Void> syncData(String processId, Mono<DataNegotiationResult> dataNegotiationResult) {
        return dataNegotiationResult.flatMap(result -> {
            Mono<MVEntity4DataNegotiation[]> allEntitiesToRequest = buildAllEntitiesToRequest(
                    Mono.just(result.newEntitiesToSync()),
                    Mono.just(result.existingEntitiesToSync())
            );

            return allEntitiesToRequest.flatMap(entities -> {
                Mono<String> issuer = Mono.just(result.issuer());

                return entitySyncWebClient.makeRequest(issuer, allEntitiesToRequest)
                        .flatMap(entitySyncResponse -> {
                            Mono<Map<Id, Entity>> entitiesById = getEntitiesById(Mono.just(entitySyncResponse));
                            Mono<Map<Id, EntityValidationData>> newEntitiesOriginalValidationDataById = getEntitiesOriginalValidationDataById(Mono.just(result.newEntitiesToSync()));
                            Mono<Map<Id, EntityValidationData>> existingEntitiesOriginalValidationDataById = getEntitiesOriginalValidationDataById(Mono.just(result.existingEntitiesToSync()));
                            Mono<List<MVEntity4DataNegotiation>> allEntitiesToRequestList = Mono.just(Arrays.asList(entities));

                            return validateEntities(processId, entitiesById, newEntitiesOriginalValidationDataById, existingEntitiesOriginalValidationDataById)
                                    .then(buildAndSaveAuditRecordFromDataSync(processId, issuer, entitiesById, allEntitiesToRequestList));
                        });
            });
        });
    }


    private Mono<Void> buildAndSaveAuditRecordFromDataSync(String processId, Mono<String> issuerMono, Mono<Map<Id, Entity>> entitiesByIdMono, Mono<List<MVEntity4DataNegotiation>> mvEntity4DataNegotiationListMono) {
        return entitiesByIdMono
                .flatMap(entitiesById -> {
                    List<Mono<Void>> auditRecordMonos = new ArrayList<>();

                    for (Map.Entry<Id, Entity> entityById : entitiesById.entrySet()) {
                        String id = entityById.getKey().value();
                        Entity entity = entityById.getValue();

                        Mono<MVEntity4DataNegotiation> currentMvEntity4DataNegotiationMono = mvEntity4DataNegotiationListMono
                                .flatMap(list -> Mono.justOrEmpty(
                                        list.stream()
                                                .filter(x -> x.id().equals(id))
                                                .findFirst()));

                        Mono<Void> auditRecordMono = issuerMono
                                .zipWith(currentMvEntity4DataNegotiationMono)
                                .flatMap(tuple -> {
                                    String issuer = tuple.getT1();
                                    MVEntity4DataNegotiation currentMvEntity4DataNegotiation = tuple.getT2();
                                    String entityValue = entity.value();
                                    return auditRecordService.buildAndSaveAuditRecordFromDataSync(processId, issuer, currentMvEntity4DataNegotiation, entityValue, AuditRecordStatus.RETRIEVED);
                                });

                        auditRecordMonos.add(auditRecordMono);
                    }

                    return Mono.when(auditRecordMonos);
                });
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
                }
                return Mono.just(entitiesById);
            } catch (JsonProcessingException e) {
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

    private Mono<Void> validateIntegrity(Mono<Map<Id, Entity>> entitiesByIdMono, Mono<Map<Id, EntityValidationData>> allEntitiesOriginalValidationDataById) {
        return entitiesByIdMono
                .flatMapIterable(Map::entrySet)
                .flatMap(entry -> {
                    Mono<String> entity = Mono.just(entry.getValue().value());
                    Id id = entry.getKey();
                    Mono<String> entityOriginalHash = allEntitiesOriginalValidationDataById.map(x -> x.get(id).hash());
                    return entity.flatMap(entityValue -> {
                        try {
                            String calculatedHash = ApplicationUtils.calculateSHA256(entityValue);
                            return entityOriginalHash.flatMap(hashValue -> {
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
                .then();
    }

    private Mono<Void> validateConsistency(String
                                                   processId, Mono<Map<Id, Entity>> entitiesByIdMono, Mono<Map<Id, EntityValidationData>> existingEntitiesOriginalValidationDataById) {
        return existingEntitiesOriginalValidationDataById
                .flatMapIterable(Map::entrySet)
                .flatMap(existingOriginalValidationDataById -> entitiesByIdMono
                        .flatMap(entities -> {
                            Entity entity = entities.get(existingOriginalValidationDataById.getKey());
                            try {
                                String id = existingOriginalValidationDataById.getKey().value();
                                String currentEntityHash = ApplicationUtils.calculateSHA256(entity.value());
                                return auditRecordService
                                        .findLatestAuditRecordForEntity(processId, id)
                                        .flatMap(auditRecord -> {
                                            if ((auditRecord.getEntityHashLink() + currentEntityHash).equals(existingOriginalValidationDataById.getValue().hashLink())) {
                                                return Mono.empty();
                                            } else {
                                                return Mono.error(new InvalidConsistencyException("The hashlink received does not correspond to that of the entity."));
                                            }
                                        });
                            } catch (NoSuchAlgorithmException e) {
                                return Mono.error(e);
                            }
                        }))
                .then();
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
