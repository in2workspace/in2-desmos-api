package es.in2.desmos.domain.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.configs.ApiConfig;
import es.in2.desmos.configs.BrokerConfig;
import es.in2.desmos.domain.exceptions.HashLinkException;
import es.in2.desmos.domain.models.BlockchainTxPayload;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlockchainTxPayloadFactoryTests {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ApiConfig apiConfig;

    @Mock
    private BrokerConfig brokerConfig;

    @InjectMocks
    private BlockchainTxPayloadFactory blockchainTxPayloadFactory;

    Map<String, Object> dataMap = new HashMap<>();


    @Test
    void testBuildBlockchainTxPayload_validData_firstHash_Success() throws Exception {
        //Arrange
        String processId = "processId";
        dataMap.put("id", "entity123");
        dataMap.put("type", "productOffering");
        dataMap.put("name", "Cloud Services Suite");
        dataMap.put("description", "Example of a Product offering for cloud services suite");
        String previousHash = "5077272d496c8afd1af9d3740f9e5f11837089b5952d577eff4c20509e6e199e";
        when(objectMapper.writeValueAsString(dataMap)).thenReturn("dataMapString");
        when(apiConfig.organizationIdHash()).thenReturn("381d18e478b9ae6e67b1bf48c9f3bcaf246d53c4311bfe81f46e63aa18167c89");
        when(brokerConfig.getEntitiesExternalDomain()).thenReturn("http://localhost:8080/entities");

        // Act
        Mono<BlockchainTxPayload> resultMono = blockchainTxPayloadFactory.buildBlockchainTxPayload(processId, dataMap, previousHash);

        // Assert
        // Check that the previous hash is the same as the hash of the data, because it is the first hash
        StepVerifier.create(resultMono)
                .assertNext(blockchainTxPayload -> Assertions.assertEquals(previousHash, ApplicationUtils.extractHashLinkFromDataLocation(blockchainTxPayload.dataLocation()))).verifyComplete();


    }

    @Test
    void testBuildBlockchainTxPayload_validData_differentHashes_Success() throws Exception {
        // Arrange
        String processId = "processId";
        dataMap.put("id", "entity123");
        dataMap.put("type", "productOffering");
        dataMap.put("name", "Cloud Services Suite");
        dataMap.put("description", "Example of a Product offering for cloud services suite");
        String previousHash = "22d0ef4e87a39c52191998f4fbf32ff672f82ed5a2b4c9902371a161402a0faf";
        when(objectMapper.writeValueAsString(dataMap)).thenReturn("dataMapString");
        when(apiConfig.organizationIdHash()).thenReturn("381d18e478b9ae6e67b1bf48c9f3bcaf246d53c4311bfe81f46e63aa18167c89");
        when(brokerConfig.getEntitiesExternalDomain()).thenReturn("http://localhost:8080/entities");

        Mono<BlockchainTxPayload> resultMono = blockchainTxPayloadFactory.buildBlockchainTxPayload(processId, dataMap, previousHash);

        // Assert
        // Check that the previous hash is different from the hash of the data, because it is not the first hash, it is the concatenation of the previous hash and the hash of the data
        StepVerifier.create(resultMono)
                .assertNext(blockchainTxPayload -> {
                    Assertions.assertNotEquals(previousHash, ApplicationUtils.extractHashLinkFromDataLocation(blockchainTxPayload.dataLocation()));
                    try {
                        Assertions.assertEquals(ApplicationUtils.calculateHashLink(previousHash, ApplicationUtils.calculateSHA256("dataMapString")), ApplicationUtils.extractHashLinkFromDataLocation(blockchainTxPayload.dataLocation()));
                    } catch (NoSuchAlgorithmException e) {
                        throw new HashLinkException("Error while calculating hash link on test");
                    }
                }).verifyComplete();

    }

    // WIP
    @Test
    void testBuildBlockchainTxPayload_invalidData_Failure() throws Exception {
        // Arrange
        String processId = "processId";
        dataMap.put("id", "entity123");
        dataMap.put("name", "Cloud Services Suite");
        dataMap.put("description", "Example of a Product offering for cloud services suite");
        String previousHash = "22d0ef4e87a39c52191998f4fbf32ff672f82ed5a2b4c9902371a161402a0faf";

        // Act
        Mono<BlockchainTxPayload> resultMono = blockchainTxPayloadFactory.buildBlockchainTxPayload(processId, dataMap, previousHash);

        // Assert
        // Check that the previous hash is different from the hash of the data, because it is not the first hash, it is the concatenation of the previous hash and the hash of the data
        StepVerifier.create(resultMono)
                .assertNext(blockchainTxPayload -> {
                    Assertions.assertThrows(HashLinkException.class, () -> {
                        ApplicationUtils.extractHashLinkFromDataLocation(blockchainTxPayload.dataLocation());
                    });
                }).verifyComplete();
    }

    @Test
    void testCalculatePreviousHashIfEmpty_validData_Success() throws Exception {
        // Arrange
        String processId = "processId";
        dataMap.put("id", "entity123");
        dataMap.put("type", "productOffering");
        dataMap.put("name", "Cloud Services Suite");
        dataMap.put("description", "Example of a Product offering for cloud services suite");
        when(objectMapper.writeValueAsString(dataMap)).thenReturn("dataMapString");

        // Act
        Mono<String> resultMono = blockchainTxPayloadFactory.calculatePreviousHashIfEmpty(processId, dataMap);

        // Assert
        // Check that the previous hash is the hash of the data
        StepVerifier.create(resultMono)
                .assertNext(previousHash -> {
                    try {
                        Assertions.assertEquals(ApplicationUtils.calculateSHA256("dataMapString"), previousHash);
                    } catch (NoSuchAlgorithmException e) {
                        throw new HashLinkException("Error while calculating hash link on test");
                    }
                }).verifyComplete();
    }

    // WIP
    @Test
    void testCalculatePreviousHashIfEmpty_invalidData_Failure() throws Exception {
        // Arrange
        String processId = "processId";
        dataMap.put("type", "productOffering");
        dataMap.put("id", "entity123");
        dataMap.put("name", "Cloud Services Suite");
        dataMap.put("description", "Example of a Product offering for cloud services suite");
        when(objectMapper.writeValueAsString(dataMap)).thenThrow(new NoSuchAlgorithmException("Error"));

        // Act
        Mono<String> resultMono = blockchainTxPayloadFactory.calculatePreviousHashIfEmpty(processId, dataMap);

        // Assert
        // Check that the previous hash is the hash of the data
        StepVerifier.create(resultMono).verifyErrorMessage("Error creating previous hash value from notification data");
    }
}
