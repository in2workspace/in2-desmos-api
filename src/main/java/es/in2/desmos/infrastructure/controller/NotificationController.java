package es.in2.desmos.infrastructure.controller;

import es.in2.desmos.application.service.DataPublicationService;
import es.in2.desmos.application.service.DataRetrievalService;
import es.in2.desmos.application.service.NotificationProcessorService;
import es.in2.desmos.domain.model.DLTNotification;
import es.in2.desmos.domain.model.BrokerNotification;
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

    private final NotificationProcessorService notificationProcessorService;

    // TODO: Implement controller data validation for BrokerNotification
    @PostMapping("/broker")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> postBrokerNotification(@RequestBody BrokerNotification brokerNotification) {
        String processId = UUID.randomUUID().toString();
        log.debug("ProcessID: {} - Broker Notification received: {}", processId, brokerNotification.toString());
        return notificationProcessorService.processBrokerNotification(processId, brokerNotification);
    }

    // TODO: Implement controller data validation for DLTNotification
    @PostMapping("/dlt")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> postDLTNotification(@RequestBody DLTNotification dltNotification) {
        String processId = UUID.randomUUID().toString();
        log.debug("ProcessID: {}, Blockchain Notification received: {}", processId, dltNotification);
        return notificationProcessorService.processDLTNotification(processId, dltNotification);
    }

}
