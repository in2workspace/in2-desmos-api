package es.in2.desmos.domain.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.configs.ApiConfig;
import es.in2.desmos.configs.BrokerConfig;
import es.in2.desmos.domain.models.BlockchainTxPayload;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Nested
@ExtendWith(MockitoExtension.class)
class BlockchainDataFactoryTests {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ApiConfig apiConfig;

    @Mock
    private BrokerConfig brokerConfig;

    @InjectMocks
    private BlockchainDataFactory blockchainDataFactory;

    private String processId;

    private String previousHash;

    @BeforeEach
    void setUp() {
        processId = "d670e989-eaf0-4916-aa8c-59d4de38b27e";
        previousHash = "68d8adfbe38fa135a0f978c96d808138ddb560d5d475f275a000bc80568d1514";
    }

    @Test
    void testValidBlockchainData() throws Exception {
        // Arrange
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", "validId");
        dataMap.put("type", "validType");

        when(objectMapper.writeValueAsString(any())).thenReturn("sampleData");
        when(apiConfig.organizationIdHash()).thenReturn("validOrganizationIdHash");
        when(brokerConfig.getEntitiesExternalDomain()).thenReturn("https://example.com/ngsi-ld/v1/entities");

        // Act
        Mono<BlockchainTxPayload> result = blockchainDataFactory.buildBlockchainData(processId, dataMap, previousHash);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testInvalidBlockchainData() throws Exception {
        // Arrange
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", "");
        dataMap.put("type", null);
        String processId = "d670e989-eaf0-4916-aa8c-59d4de38b27e";
        String previousHash = "68d8adfbe38fa135a0f978c96d808138ddb560d5d475f275a000bc80568d1514";

        when(objectMapper.writeValueAsString(any())).thenReturn("sampleData");
        when(apiConfig.organizationIdHash()).thenReturn("validOrganizationIdHash");
        when(brokerConfig.getEntitiesExternalDomain()).thenReturn("https://example.com/ngsi-ld/v1/entities");

        // Act
        Mono<BlockchainTxPayload> result = blockchainDataFactory.buildBlockchainData(processId, dataMap, previousHash);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> {
                    if (throwable instanceof ConstraintViolationException cve) {
                        for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                            System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
                        }
                        return true;
                    }
                    return false;
                })
                .verify();
    }

}