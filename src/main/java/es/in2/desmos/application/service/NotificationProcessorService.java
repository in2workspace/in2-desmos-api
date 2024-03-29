package es.in2.desmos.application.service;

import es.in2.desmos.domain.model.BrokerNotification;
import es.in2.desmos.domain.model.DLTNotification;
import reactor.core.publisher.Mono;

public interface NotificationProcessorService {

    Mono<Void> processBrokerNotification(String processId, BrokerNotification brokerNotification);

    Mono<Void> processDLTNotification(String processId, DLTNotification dltNotification);

}
