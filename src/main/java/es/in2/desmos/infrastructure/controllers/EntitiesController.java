package es.in2.desmos.infrastructure.controllers;

import es.in2.desmos.domain.services.broker.BrokerListenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/entities")
@RequiredArgsConstructor
public class EntitiesController {

    private final BrokerListenerService brokerListenerService;

    @GetMapping("/{id}")
    public Mono<String> getEntities(@PathVariable String id) {
        String processId = UUID.randomUUID().toString();
        return brokerListenerService.getEntityById(processId, id);
    }

}
