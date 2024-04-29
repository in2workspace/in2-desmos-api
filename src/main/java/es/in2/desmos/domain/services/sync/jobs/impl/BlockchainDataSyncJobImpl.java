package es.in2.desmos.domain.services.sync.jobs.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exceptions.JsonReadingException;
import es.in2.desmos.domain.models.AuditRecord;
import es.in2.desmos.domain.models.AuditRecordStatus;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.services.api.AuditRecordService;
import es.in2.desmos.domain.services.blockchain.adapter.BlockchainAdapterService;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.domain.services.sync.jobs.BlockchainDataSyncJob;
import es.in2.desmos.domain.services.sync.services.DataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockchainDataSyncJobImpl implements BlockchainDataSyncJob {

    private final AuditRecordService auditRecordService;
    private final BlockchainAdapterService blockchainAdapterService;
    private final ObjectMapper objectMapper;
    private final DataSyncService dataSyncService;
    private final BrokerPublisherService brokerPublisherService;

    @Override
    public Flux<Void> startBlockchainDataSyncJob(String processId) {
        log.info("Starting the Blockchain Data Sync Job...");
        // We need to find the last published audit record to know where to start querying the DLT Adapter
        return auditRecordService.findLatestConsumerPublishedAuditRecord(processId)
                .collectList()
                .flatMapMany(auditRecords -> {
                    if (auditRecords.isEmpty()) {
                        // If there are no audit records,
                        // we need to start querying the DLT Adapter from the beginning
                        return getAllTransactions(processId);
                    } else {
                        // If there are audit records,
                        // we need to start querying the DLT Adapter from the last published audit record
                        return getTransactionsFromRangeOfTime(auditRecords.get(0), processId);
                    }
                })
                // Deserialize the response from the DLT Adapter
                .flatMap(response -> deserializeBlockchainNotifications(processId, response).buffer(50)
                        .filter(notificationList -> !notificationList.isEmpty())
                        .flatMap(notificationList -> Flux.fromIterable(notificationList)
                                .flatMap(blockchainNotification ->
                                        // Create an audit record for the received notification, Status: RECEIVED
                                        auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, null, AuditRecordStatus.RECEIVED)
                                                // todo: consider to add this blockchainNotification as en event of the queue with High priority.
                                                //  These lines of code are duplicated from the SubscribeWorkflowImpl(51-62)
                                                .then(dataSyncService.getEntityFromExternalSource(processId, blockchainNotification)
                                                        // verify the data integrity of the retrieved entity
                                                        .flatMap(retrievedBrokerEntity ->
                                                                // Verify the integrity and consistency of the retrieved entity
                                                                dataSyncService.verifyRetrievedEntityData(processId, blockchainNotification, retrievedBrokerEntity)
                                                                        // Build and save the audit record for RETRIEVED status
                                                                        .then(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, AuditRecordStatus.RETRIEVED))
                                                                        // Publish the retrieved entity to the local broker
                                                                        .then(brokerPublisherService.publishEntityToContextBroker(processId, blockchainNotification, retrievedBrokerEntity))
                                                                        // Build and save the audit record for PUBLISHED status
                                                                        .then(auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, AuditRecordStatus.PUBLISHED))
                                                        )
                                                )
                                ))
                );
    }

    private Flux<String> getAllTransactions(String processId) {
        long nowTimestampInMillisAndUnixFormat = Instant.now().toEpochMilli();
        return blockchainAdapterService.getEventsFromRangeOfTime(processId, 0, nowTimestampInMillisAndUnixFormat);
    }

    private Flux<String> getTransactionsFromRangeOfTime(AuditRecord auditRecord, String processId) {
        long startTimestampInMillisAndUnixFormat = auditRecord.getCreatedAt().getTime();
        long endTimestampInMillisAndUnixFormat = Instant.now().toEpochMilli();
        return blockchainAdapterService
                .getEventsFromRangeOfTime(processId, startTimestampInMillisAndUnixFormat, endTimestampInMillisAndUnixFormat);
    }

    private Flux<BlockchainNotification> deserializeBlockchainNotifications(String processId, String responseList) {
        log.debug("ProcessID: {} - Deserializing response from DLT Adapter: {}", processId, responseList);
        try {
            List<BlockchainNotification> notificationList = objectMapper.readValue(responseList, new TypeReference<>() {
            });
            return Flux.fromIterable(notificationList);
        } catch (JsonProcessingException e) {
            log.warn("ProcessID: {} - Error processing JSON: {}", processId, responseList, e);
            throw new JsonReadingException("Error deserializing response from DLT Adapter");
        }
    }

}
