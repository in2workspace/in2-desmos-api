package es.in2.desmos.domain.services.broker;

import es.in2.desmos.domain.models.BrokerNotification;
import es.in2.desmos.domain.models.BrokerSubscription;
import reactor.core.publisher.Mono;

public interface BrokerListenerService {

    Mono<Void> createSubscription(String processId, BrokerSubscription brokerSubscription);

    Mono<Void> processBrokerNotification(String processId, BrokerNotification brokerNotification);

}
