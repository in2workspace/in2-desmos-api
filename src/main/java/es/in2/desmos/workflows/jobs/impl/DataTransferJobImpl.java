package es.in2.desmos.workflows.jobs.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataTransferJobImpl implements DataTransferJob {
    private final EntitySyncWebClient entitySyncWebClient;
    private final AuditRecordService auditRecordService;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> syncData(Mono<DataNegotiationResult> dataNegotiationResult) {

        return dataNegotiationResult
                .map(result -> );

        Mono<String> issuer = dataNegotiationResult.map(DataNegotiationResult::issuer);

        Mono<List<MVEntity4DataNegotiation>> allEntitiesToRequest = getAllEntitiesToRequest(dataNegotiationResult);

        Mono<EntitySyncRequest> entitySyncRequest = buildEntitiesToSyncRequest(allEntitiesToRequest);

        return entitySyncWebClient.makeRequest(issuer, entitySyncRequest)
                .doOnNext(entitySyncResponse -> {
                            Mono<Map<String, String>> entitiesById = getEntitiesById(entitySyncResponse.map(EntitySyncResponse::entities));

                            Mono<Map<String, EntityValidationData>> entitiesOriginalValidationDataById = getEntitiesOriginalValidationDataById(allEntitiesToRequest);

                            validateEntities(entitiesById, entitiesOriginalValidationDataById);
                        }
                );


        return Mono.empty();
    }

    private Mono<Map<String, EntityValidationData>> getEntitiesOriginalValidationDataById(Mono<List<MVEntity4DataNegotiation>> allEntitiesToRequest) {
        return allEntitiesToRequest.map(x -> {
            Map<String, EntityValidationData> entityValidationDataMap = new HashMap<>();
            for (var entity : x) {
                entityValidationDataMap.put(entity.id(), new EntityValidationData(entity.hash(), entity.hashlink()));
            }
            return entityValidationDataMap;
        });
    }

    private Mono<List<MVEntity4DataNegotiation>> getAllEntitiesToRequest(Mono<DataNegotiationResult> dataNegotiationResult) {
        return dataNegotiationResult
                .map(x ->
                        Stream.concat(x.newEntitiesToSync().stream(), x.existingEntitiesToSync().stream()).toList());
    }

    private Mono<Map<String, String>> getEntitiesById(Mono<String> entities) {
        return entities.flatMap(entitiesValue -> {
            try {
                Map<String, String> entitiesById = new HashMap<>();
                JsonNode entitiesJson = objectMapper.readTree(entitiesValue);
                if (entitiesJson.isArray()) {
                    entitiesJson.forEach(objNode -> {
                        String currentEntityId = objNode.get("id").asText();
                        entitiesById.put(currentEntityId, objNode.toString());
                    });
                }
                return Mono.just(entitiesById);
            } catch (JsonProcessingException e) {
                return Mono.error(e);
            }
        });
    }

    private Mono<Void> validateEntities(Mono<Map<String, String>> entitiesByIdMono, Mono<Map<String, EntityValidationData>> entitiesOriginalValidationDataById) {
        return entitiesByIdMono
                .flatMapIterable(Map::entrySet)
                .flatMap(entry -> {
                    Mono<String> entity = Mono.just(entry.getValue());
                    String id = entry.getKey();
                    Mono<String> entityValidationData = entitiesOriginalValidationDataById.map(x -> x.get(id).hash());
                    return validateIntegrity(entity, entityValidationData);
                })
                .then();
    }

    private Mono<Void> validateIntegrity(Mono<String> entity, Mono<String> hash) {
        return entity.flatMap(entityValue -> {
            try {
                String calculatedHash = ApplicationUtils.calculateSHA256(entityValue);
                return hash.flatMap(hashValue -> {
                    if (calculatedHash.equals(hashValue)) {
                        return Mono.empty();
                    } else {
                        return Mono.error(new InvalidIntegrityException("The hash received at the origin is different from the actual hash of the entity."));
                    }
                });
            } catch (NoSuchAlgorithmException e) {
                return Mono.error(e);
            }
        });
    }

    private Mono<EntitySyncRequest> buildEntitiesToSyncRequest(Mono<List<MVEntity4DataNegotiation>> entitiesToRequest) {
        return entitiesToRequest.map(EntitySyncRequest::new);
    }
}
