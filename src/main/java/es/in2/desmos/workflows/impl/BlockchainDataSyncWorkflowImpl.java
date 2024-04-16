package es.in2.desmos.workflows.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exceptions.JsonReadingException;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.api.BrokerEntityRetrievalService;
import es.in2.desmos.domain.services.api.BrokerEntityVerifyService;
import es.in2.desmos.domain.services.blockchain.BlockchainListenerService;
import es.in2.desmos.domain.services.blockchain.adapter.BlockchainAdapterService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.workflows.BlockchainDataSyncWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockchainDataSyncWorkflowImpl implements BlockchainDataSyncWorkflow {

    private final AuditRecordService auditRecordService;
    private final BlockchainAdapterService blockchainAdapterService;
    private final ObjectMapper objectMapper;
    private final BrokerEntityRetrievalService brokerEntityRetrievalService;
    private final BrokerEntityVerifyService brokerEntityVerifyService;
    private final BrokerPublisherService brokerPublisherService;
    private final BlockchainListenerService blockchainListenerService;

    @Override
    //todo: Add the case where the PUBLISHED auditRecords exist
    public Flux<Void> startBlockchainDataSyncWorkflow(String processId) {
        log.info("Starting the Blockchain Data Sync Workflow...");
        return auditRecordService.findLatestConsumerPublishedAuditRecord(processId)
                .collectList()
                .flatMapMany(auditRecords -> {
                    if (auditRecords.isEmpty()) {
                        return queryDLTAdapterFromBeginning(processId);
                    } else {
                        return queryDLTAdapterFromLastPublished(auditRecords.get(0), processId);
                    }
                })
                .flatMap(response -> deserializeBlockchainNotifications(processId, response).buffer(50)
                        .filter(batch -> !batch.isEmpty())
                        .flatMap(batch -> Flux.fromIterable(batch)
                                .flatMap(blockchainNotification ->
                                        auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, null, AuditRecordStatus.RECEIVED)
                                                .then(brokerEntityRetrievalService.retrieveEntityFromExternalBroker(processId, blockchainNotification)
                                                        .flatMap(retrievedBrokerEntity ->
                                                                brokerEntityVerifyService.verifyRetrievedEntityDataIntegrity(processId, blockchainNotification, retrievedBrokerEntity)
                                                                        .then(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, AuditRecordStatus.RETRIEVED))
                                                                        .then(brokerPublisherService.publishDataToBroker(processId, blockchainNotification, retrievedBrokerEntity))
                                                                        .then(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, AuditRecordStatus.PUBLISHED))
                                                        ))
                                ))

                );
    }

    private Flux<String> queryDLTAdapterFromBeginning(String processId) {
        // long startUnixTimestampMillis = Instant.EPOCH.toEpochMilli(); // For debug purposes, the range of events will be shorter
        long nowUnixTimestampMillis = Instant.now().toEpochMilli();
        return blockchainAdapterService.getEventsFromRange(processId, 1713266146360L, nowUnixTimestampMillis);
    }

    private Flux<String> queryDLTAdapterFromLastPublished(AuditRecord auditRecord, String processId) {
        long startUnixTimestampMillis = auditRecord.getCreatedAt().getTime();
        long nowUnixTimestampMillis = Instant.now().toEpochMilli();
        return blockchainAdapterService.getEventsFromRange(processId, startUnixTimestampMillis, nowUnixTimestampMillis);
    }

    private Flux<BlockchainNotification> deserializeBlockchainNotifications(String processId, String responseList) {
        try {
            log.debug("ProcessID: {} - Deserializing response from DLT Adapter: {}", processId, responseList);
            List<BlockchainNotification> notifications = objectMapper.readValue(responseList, new TypeReference<>() {
            });
            return Flux.fromIterable(notifications);
        } catch (JsonProcessingException e) {
            throw new JsonReadingException("Error deserializing response from DLT Adapter");
        }
    }


}
