package es.in2.desmos.controllers;

import es.in2.desmos.domain.model.BlockchainNotification;
import es.in2.desmos.domain.model.BrokerNotification;
import es.in2.desmos.services.blockchain.BlockchainListenerService;
import es.in2.desmos.services.broker.BrokerListenerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final BrokerListenerService brokerListenerService;
    private final BlockchainListenerService blockchainListenerService;

    @PostMapping("/broker")
    @ResponseStatus(HttpStatus.OK)
    // todo: add @Valid annotation to validate the request body
    public Mono<Void> postBrokerNotification(@RequestBody BrokerNotification brokerNotification) {
        String processId = UUID.randomUUID().toString();
        log.debug("ProcessID: {} - Broker Notification received: {}", processId, brokerNotification.toString());
        return brokerListenerService.processBrokerNotification(processId, brokerNotification);
    }

    @PostMapping("/dlt")
    @ResponseStatus(HttpStatus.OK)
    // todo: add @Valid annotation to validate the request body
    public Mono<Void> postDLTNotification(@RequestBody BlockchainNotification blockchainNotification) {
        String processId = UUID.randomUUID().toString();
        log.debug("ProcessID: {}, Blockchain Notification received: {}", processId, blockchainNotification);
        return blockchainListenerService.processBlockchainNotification(processId, blockchainNotification);
    }

}
