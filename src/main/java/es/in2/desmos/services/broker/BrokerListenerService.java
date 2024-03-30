package es.in2.desmos.services.broker;

import es.in2.desmos.domain.model.BrokerNotification;
import es.in2.desmos.domain.model.BrokerSubscription;
import reactor.core.publisher.Mono;

public interface BrokerListenerService {

    Mono<Void> createSubscription(String processId, BrokerSubscription brokerSubscription);

    Mono<Void> processBrokerNotification(String processId, BrokerNotification brokerNotification);

}
