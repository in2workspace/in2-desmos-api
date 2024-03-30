package es.in2.desmos.services.broker;

import reactor.core.publisher.Flux;

public interface BrokerPublisherService {

    Flux<Void> publishDataToBroker();

}
