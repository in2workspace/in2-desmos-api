package es.in2.desmos.domain.services.api.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exceptions.HashLinkException;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.services.api.BrokerEntityVerifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;

import static es.in2.desmos.domain.utils.ApplicationUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerEntityVerifyServiceImpl implements BrokerEntityVerifyService {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<String> verifyRetrievedEntityDataIntegrity(String processId, BlockchainNotification blockchainNotification, String retrievedBrokerEntity) {
        log.debug("ProcessID: {} - Verifying the data integrity of the retrieved entity...", processId);
        try {
            // We want to alphabetically sort the attributes of the retrieved broker entity
            retrievedBrokerEntity = sortAttributesAlphabetically(retrievedBrokerEntity);
            // Calculate the SHA-256 hash of the retrieved broker entity
            String retrievedEntityHash = calculateSHA256(retrievedBrokerEntity);
            // Extract the hashLink (value) from the BlockchainNotification.dataLocation field.
            // This hashLink is expected to match with the new calculated hashLink, that is,
            // the hash resulting from the concatenation of the BlockchainNotification.previousHash field
            // and the calculated hash of the retrieved broker entity.
            // The previousHash field starts with "0x" and the hashLink in the dataLocation field does not.
            String previousEntityHash = blockchainNotification.previousEntityHash().substring(2);
            String expectedEntityHasLink = extractHashLinkFromDataLocation(blockchainNotification.dataLocation());
            // WIP: Calculates the hashlink of the retrieved entity hash and the previous entity hash if the previous entity hash is different from the hashlink in the dataLocation
            String calculatedEntityHasLink = previousEntityHash.equals(extractHashLinkFromDataLocation(blockchainNotification.dataLocation()))
                    ? previousEntityHash : calculateHashLink(previousEntityHash, retrievedEntityHash);
            // If the calculated hashLink does not match the expected hashLink, an exception is thrown.
            log.debug("ProcessID: {} - Retrieved Entity Hash: {}", processId, retrievedEntityHash);
            log.debug("ProcessID: {} - Calculated HashLink: {}", processId, calculatedEntityHasLink);
            log.debug("ProcessID: {} - Expected HashLink: {}", processId, expectedEntityHasLink);
            if (!calculatedEntityHasLink.equals(expectedEntityHasLink)) {
                log.error("ProcessID: {} - Error occurred while verifying the data integrity of the retrieved entity: HashLink verification failed", processId);
                return Mono.error(new HashLinkException("HashLink verification failed"));
            }
            // If the hashLink verification is successful, the retrieved entity is returned.
            else {
                log.debug("ProcessID: {} - Data integrity of the retrieved entity verified successfully", processId);
                return Mono.just(retrievedBrokerEntity);
            }
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            log.error("ProcessID: {} - Error occurred while sorting the attributes of the retrieved entity: {}", processId, e.getMessage());
            return Mono.error(e);
        }
    }

    private String sortAttributesAlphabetically(String retrievedBrokerEntity) throws JsonProcessingException {
        // Sort alphabetically the attributes of the retrieved broker entity
        JsonNode retrievedBrokerEntityJson = objectMapper.readTree(retrievedBrokerEntity);
        return objectMapper.writeValueAsString(retrievedBrokerEntityJson);
    }
}
