package es.in2.desmos.api.service;


import es.in2.desmos.api.model.BlockchainNotification;
import es.in2.desmos.api.model.BrokerNotification;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface NotificationProcessorService {

    Mono<Map<String, Object>> processBrokerNotification(String processId, BrokerNotification brokerNotification);

    Mono<Void> processBlockchainNotification(String processId, BlockchainNotification blockchainNotification);

}
