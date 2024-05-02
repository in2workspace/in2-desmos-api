package es.in2.desmos.domain.services.blockchain;

import es.in2.desmos.domain.models.BlockchainTxPayload;
import reactor.core.publisher.Mono;

public interface BlockchainPublisherService {

    Mono<Void> PublishBlockchainTxPayloadToDltAdapter(String processId, BlockchainTxPayload blockchainTxPayload);
}