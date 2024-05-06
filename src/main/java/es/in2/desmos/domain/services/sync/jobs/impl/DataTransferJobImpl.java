package es.in2.desmos.domain.services.sync.jobs.impl;


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
import es.in2.desmos.domain.services.sync.jobs.DataTransferJob;
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
    public Mono<Void> syncDataFromList(String processId, Mono<List<DataNegotiationResult>> dataNegotiationResult) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> syncData(String processId, Mono<DataNegotiationResult> dataNegotiationResult) {
        return dataNegotiationResult.flatMap(result -> {
            log.info("ProcessID: {} - Starting Data Transfer Job", processId);

            log.debug("ProcessID: {} - Issuer: {}", processId, result.issuer());
            log.debug("ProcessID: {} - New Entities to Sync: {}", processId, result.newEntitiesToSync());
            log.debug("ProcessID: {} - Existing Entities to Sync: {}", processId, result.existingEntitiesToSync());

            Mono<String> issuer = Mono.just(result.issuer());

            Mono<Id[]> allEntitiesToRequest = buildAllEntitiesToRequest(
                    Mono.just(result.newEntitiesToSync()),
                    Mono.just(result.existingEntitiesToSync())
            );

            return allEntitiesToRequest.flatMap(entities -> entitySyncWebClient.makeRequest(processId, issuer, allEntitiesToRequest)
                    .flatMap(entitySyncResponse -> {
                        Mono<String> entitySyncResponseMono = Mono.just(entitySyncResponse);
                        return getEntitiesById(entitySyncResponseMono)
                                .flatMap(entitiesById -> {
                                    Mono<Map<Id, Entity>> entitiesByIdMono = Mono.just(entitiesById);

                                    Mono<Map<Id, EntityValidationData>> newEntitiesOriginalValidationDataById = getEntitiesOriginalValidationDataById(Mono.just(result.newEntitiesToSync()));
                                    Mono<Map<Id, EntityValidationData>> existingEntitiesOriginalValidationDataById = getEntitiesOriginalValidationDataById(Mono.just(result.existingEntitiesToSync()));

                                    Mono<List<MVEntity4DataNegotiation>> allMVEntity4DataNegotiation = buildAllMVEntities4DataNegotiation(
                                            Mono.just(result.newEntitiesToSync()),
                                            Mono.just(result.existingEntitiesToSync())
                                    );

                                    return validateEntities(processId, entitiesByIdMono, newEntitiesOriginalValidationDataById, existingEntitiesOriginalValidationDataById)
                                            .then(buildAndSaveAuditRecordFromDataSync(processId, issuer, entitiesByIdMono, allMVEntity4DataNegotiation, AuditRecordStatus.RETRIEVED))
                                            .then(batchUpsertEntitiesToContextBroker(processId, entitySyncResponseMono))
                                            .then(buildAndSaveAuditRecordFromDataSync(processId, issuer, entitiesByIdMono, allMVEntity4DataNegotiation, AuditRecordStatus.PUBLISHED));
                                });
                    }));
        });
    }

    private Mono<Id[]> buildAllEntitiesToRequest(Mono<List<MVEntity4DataNegotiation>> newEntitiesToSyncMono, Mono<List<MVEntity4DataNegotiation>> existingEntitiesToSyncMono) {
        return newEntitiesToSyncMono.zipWith(existingEntitiesToSyncMono)
                .flatMap(tuple ->
                        Mono.just(Stream.concat(tuple.getT1().stream().map(x -> new Id(x.id())), tuple.getT2().stream().map(x -> new Id(x.id()))).toArray(Id[]::new)));
    }

    private Mono<List<MVEntity4DataNegotiation>> buildAllMVEntities4DataNegotiation(Mono<List<MVEntity4DataNegotiation>> newEntitiesToSyncMono, Mono<List<MVEntity4DataNegotiation>> existingEntitiesToSyncMono) {
        return newEntitiesToSyncMono.zipWith(existingEntitiesToSyncMono)
                .flatMap(tuple ->
                        Mono.just(Stream.concat(tuple.getT1().stream(), tuple.getT2().stream()).toList()));
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
            } catch (JsonProcessingException e) {
                return Mono.error(e);
            }
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

    private Mono<Void> validateEntities(String processId, Mono<Map<Id, Entity>> entitiesByIdMono, Mono<Map<Id, EntityValidationData>> newEntitiesOriginalValidationDataById, Mono<Map<Id, EntityValidationData>> existingEntitiesOriginalValidationDataById) {
        Mono<Map<Id, EntityValidationData>> allEntitiesOriginalValidationDataById =
                concatTwoEntitiesOriginalValidationDataByIdMaps(
                        newEntitiesOriginalValidationDataById,
                        existingEntitiesOriginalValidationDataById);

        return validateIntegrity(entitiesByIdMono, allEntitiesOriginalValidationDataById)
                .then(validateConsistency(processId, entitiesByIdMono, existingEntitiesOriginalValidationDataById));
    }

    private Mono<Map<Id, EntityValidationData>> concatTwoEntitiesOriginalValidationDataByIdMaps
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

    private Mono<Void> validateIntegrity(Mono<Map<Id, Entity>> entitiesByIdMono, Mono<Map<Id, EntityValidationData>> allEntitiesExistingValidationDataById) {
        return entitiesByIdMono
                .flatMapIterable(Map::entrySet)
                .flatMap(entry -> {
                    Mono<String> entity = Mono.just(entry.getValue().value());
                    Id id = entry.getKey();
                    Mono<String> entityRcvdHash = allEntitiesExistingValidationDataById.map(x -> x.get(id).hash());
                    Mono<String> calculatedHashMono = calculateHash(entity);
                    return calculatedHashMono.flatMap(calculatedHash ->
                            entityRcvdHash.flatMap(hashValue -> {
                                if (calculatedHash.equals(hashValue)) {
                                    return Mono.empty();
                                } else {
                                    log.error("expected hash: {}\ncurrent hash: {}", calculatedHash, hashValue);
                                    return Mono.error(new InvalidIntegrityException("The hash received at the origin is different from the actual hash of the entity."));
                                }
                            }));

                })
                .collectList()
                .then();
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

}
