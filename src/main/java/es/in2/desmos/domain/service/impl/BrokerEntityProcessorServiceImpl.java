package es.in2.desmos.domain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.exception.JsonReadingException;
import es.in2.desmos.domain.service.BrokerEntityProcessorService;
import es.in2.desmos.infrastructure.broker.service.BrokerPublicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerEntityProcessorServiceImpl implements BrokerEntityProcessorService {

    private final ObjectMapper objectMapper;
    private final BrokerPublicationService brokerPublicationService;

    @Override
    public Mono<Map<String, Object>> processBrokerEntity(String processId, String brokerEntityId) {
        return brokerPublicationService.getEntityById(processId, brokerEntityId)
                .flatMap(response -> {
                    try {
                        Map<String, Object> dataMap = objectMapper.readValue(response, new TypeReference<>() {
                        });
                        return Mono.just(dataMap);
                    } catch (JsonProcessingException e) {
                        return Mono.error(new JsonReadingException("Error while processing entities."));
                    }
                })
                .doOnSuccess(dataMap -> log.info("Broker Entity processed successfully."))
                .doOnError(error -> log.error("Error processing Broker Entity: {}", error.getMessage(), error));
    }

}

