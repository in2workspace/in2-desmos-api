package es.in2.desmos.objectmothers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exceptions.JsonReadingException;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.utils.ApplicationUtils;

import java.security.NoSuchAlgorithmException;

public final class BlockchainNotificationMother {
    private BlockchainNotificationMother() {

    }

    public static BlockchainNotification Empty() {
       return BlockchainNotification.builder()
                    .build();
    }

    public static BlockchainNotification FromBrokerDataMother(String brokerData) throws NoSuchAlgorithmException {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonEntity = objectMapper.readTree(brokerData);
            String entityId = jsonEntity.get("id").asText();
            String entityIdHash = ApplicationUtils.calculateSHA256(entityId);

            return BlockchainNotification.builder()
                    .entityId("0x" + entityIdHash)
                    .build();
        } catch (JsonProcessingException e) {
            throw new JsonReadingException(e.getMessage());
        }
    }
}
