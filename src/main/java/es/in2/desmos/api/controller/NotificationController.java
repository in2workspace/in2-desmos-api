package es.in2.desmos.api.controller;

import es.in2.desmos.api.facade.BlockchainToBrokerSynchronizer;
import es.in2.desmos.api.facade.BrokerToBlockchainPublisher;
import es.in2.desmos.api.model.BlockchainNotification;
import es.in2.desmos.api.model.BrokerNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final BrokerToBlockchainPublisher brokerToBlockchainPublisher;
    private final BlockchainToBrokerSynchronizer blockchainToBrokerSynchronizer;

    @PostMapping("/broker")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> captureBrokerNotification(@RequestBody BrokerNotification brokerNotification) {
        String processId = UUID.randomUUID().toString();
        log.debug("ProcessID: {} - Broker Notification received: {}", processId, brokerNotification.toString());
        return brokerToBlockchainPublisher.processAndPublishBrokerNotificationToBlockchain(processId, brokerNotification);
    }

    @PostMapping("/dlt")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> captureBlockchainNotification(@RequestBody BlockchainNotification blockchainNotification) {
        String processId = UUID.randomUUID().toString();
        log.debug("ProcessID: {}, Blockchain Notification received: {}", processId, blockchainNotification);
        return blockchainToBrokerSynchronizer.retrieveAndPublishEntityToBroker(processId, blockchainNotification);
    }

}
