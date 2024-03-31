//package es.in2.desmos.todo.domain.service;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import es.in2.desmos.domain.exceptions.JsonReadingException;
//import es.in2.desmos.domain.services.api.impl.BrokerEntityProcessorServiceImpl;
//import es.in2.desmos.z.services.BrokerPublicationService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.util.Map;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class BrokerEntityProcessorServiceTests {
//
//    @Mock
//    private BrokerPublicationService brokerPublicationService;
//
//    @Mock
//    private ObjectMapper objectMapper;
//
//    @InjectMocks
//    private BrokerEntityProcessorServiceImpl service;
//
//    @Test
//    void processBrokerEntityShouldReturnDataMapOnSuccess() throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {
//        String processId = "testProcessId";
//        String brokerEntityId = "testEntityId";
//        String jsonResponse = "{\"key\":\"value\"}";
//        Map<String, Object> expectedDataMap = Map.of("key", "value");
//
//        when(brokerPublicationService.getEntityById(processId, brokerEntityId)).thenReturn(Mono.just(jsonResponse));
//        when(objectMapper.readValue(eq(jsonResponse), any(TypeReference.class))).thenReturn(expectedDataMap);
//
//        StepVerifier.create(service.processBrokerEntity(processId, brokerEntityId))
//                .expectNext(expectedDataMap)
//                .verifyComplete();
//    }
//
//    @Test
//    void processBrokerEntityShouldErrorOnJsonProcessingException() throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {
//        String processId = "testProcessId";
//        String brokerEntityId = "testEntityId";
//        String invalidJsonResponse = "invalid json";
//
//        when(brokerPublicationService.getEntityById(processId, brokerEntityId)).thenReturn(Mono.just(invalidJsonResponse));
//        when(objectMapper.readValue(eq(invalidJsonResponse), any(TypeReference.class))).thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("Error processing JSON") {});
//
//        StepVerifier.create(service.processBrokerEntity(processId, brokerEntityId))
//                .expectErrorMatches(throwable -> throwable instanceof JsonReadingException)
//                .verify();
//    }
//
//
//}
