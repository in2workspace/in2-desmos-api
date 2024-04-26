//package es.in2.desmos.domain.services.api.impl;
//
//import es.in2.desmos.configs.ApiConfig;
//import es.in2.desmos.domain.exceptions.BrokerEntityRetrievalException;
//import es.in2.desmos.domain.models.BlockchainNotification;
//import es.in2.desmos.domain.services.api.ExternalEntityRetrievalService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//import static es.in2.desmos.domain.utils.ApplicationUtils.extractContextBrokerUrlFromDataLocation;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ExternalEntityRetrievalServiceImpl implements ExternalEntityRetrievalService {
//
//    private final ApiConfig apiConfig;
//
//    /*
//     *  This method retrieves the entity from the external broker executing the URL provided in the dataLocation
//     *  field of the BlockchainNotification.
//     *  If the entity is successfully retrieved, the method follows by checking the data integrity of itself.
//     */
//    @Override
//    public Mono<String> getEntityFromExternalSource(String processId, BlockchainNotification blockchainNotification) {
//        log.debug("ProcessID: {} - Retrieving entity from the external broker...", processId);
//        // Get the External Broker URL from the dataLocation
//        String externalBrokerURL = extractContextBrokerUrlFromDataLocation(blockchainNotification.dataLocation());
//        log.debug("ProcessID: {} - External Broker URL: {}", processId, externalBrokerURL);
//        // Retrieve entity from the External Broker
//        return apiConfig.webClient()
//                .get()
//                .uri(externalBrokerURL)
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .onStatus(status -> status != null && status.isSameCodeAs(HttpStatusCode.valueOf(200)),
//                        clientResponse -> {
//                            log.debug("ProcessID: {} - Entity retrieved successfully from the external broker", processId);
//                            return Mono.empty();
//                        })
//                .onStatus(status -> status != null && status.is4xxClientError(),
//                        clientResponse -> {
//                            throw new BrokerEntityRetrievalException("Error occurred while retrieving entity from the external broker");
//                        })
//                .onStatus(status -> status != null && status.is5xxServerError(),
//                        clientResponse -> {
//                            throw new BrokerEntityRetrievalException("Error occurred while retrieving entity from the external broker");
//                        })
//                .bodyToMono(String.class);
//    }
//
//}
