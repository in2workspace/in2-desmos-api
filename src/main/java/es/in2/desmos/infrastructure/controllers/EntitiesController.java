package es.in2.desmos.infrastructure.controllers;

import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/entities")
@RequiredArgsConstructor
public class EntitiesController {

    private final BrokerPublisherService brokerPublisherService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<String> getEntities(@PathVariable String id) {
        String processId = UUID.randomUUID().toString();
        return brokerPublisherService.getEntityById(processId, id);
    }

}
