package es.in2.desmos.api.controller;

import es.in2.desmos.api.model.BlockchainNotification;
import es.in2.desmos.api.model.BrokerNotification;
import es.in2.desmos.api.service.NotificationProcessorService;
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

    private final NotificationProcessorService notificationProcessorService;

    @PostMapping("/broker")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> captureBrokerNotification(@RequestBody BrokerNotification brokerNotification) {
        String processId = UUID.randomUUID().toString();
        log.debug("ProcessID: {} - Broker Notification received: {}", processId, brokerNotification.toString());
        return notificationProcessorService.detectBrokerNotificationPriority(processId, brokerNotification);
    }

    @PostMapping("/dlt")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> captureBlockchainNotification(@RequestBody BlockchainNotification blockchainNotification) {
        String processId = UUID.randomUUID().toString();
        log.debug("ProcessID: {}, Blockchain Notification received: {}", processId, blockchainNotification);
        return notificationProcessorService.detectBlockchainNotificationPriority(processId, blockchainNotification);
    }

}
