package es.in2.desmos.api.controller;

import es.in2.desmos.api.facade.BlockchainToBrokerSynchronizer;
import es.in2.desmos.api.facade.BrokerToBlockchainPublisher;
import es.in2.desmos.api.model.BlockchainNotification;
import es.in2.desmos.api.model.BrokerNotification;
import es.in2.desmos.api.model.EventQueue;
import es.in2.desmos.api.model.EventQueuePriority;
import es.in2.desmos.api.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final BrokerToBlockchainPublisher brokerToBlockchainPublisher;
    private final BlockchainToBrokerSynchronizer blockchainToBrokerSynchronizer;
    private final QueueService blockchainToBrokerQueueService;
    private final QueueService brokerToBlockchainQueueService;

    @PostMapping("/broker")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> captureBrokerNotification(@RequestBody BrokerNotification brokerNotification) {
        String processId = UUID.randomUUID().toString();
        log.debug("ProcessID: {} - Broker Notification received: {}", processId, brokerNotification.toString());
        return brokerToBlockchainQueueService.enqueueEvent(EventQueue.builder()
                .event(Collections.singletonList(brokerNotification))
                .priority(EventQueuePriority.PUBLICATION)
                .build());
    }

    @PostMapping("/dlt")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> captureBlockchainNotification(@RequestBody BlockchainNotification blockchainNotification) {
        String processId = UUID.randomUUID().toString();
        log.debug("ProcessID: {}, Blockchain Notification received: {}", processId, blockchainNotification);
        return blockchainToBrokerQueueService.enqueueEvent(EventQueue.builder()
                .event(Collections.singletonList(blockchainNotification))
                .priority(EventQueuePriority.PUBLICATION)
                .build());
    }

}
