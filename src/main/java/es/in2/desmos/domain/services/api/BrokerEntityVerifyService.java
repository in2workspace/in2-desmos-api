package es.in2.desmos.domain.services.api;

import es.in2.desmos.domain.models.BlockchainNotification;
import reactor.core.publisher.Mono;

public interface BrokerEntityVerifyService {
    Mono<String> verifyRetrievedEntityDataIntegrity(String processId, BlockchainNotification blockchainNotification, String retrievedBrokerEntity);
}
