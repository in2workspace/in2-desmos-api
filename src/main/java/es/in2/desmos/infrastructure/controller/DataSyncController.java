package es.in2.desmos.infrastructure.controller;

import es.in2.desmos.application.service.DataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/sync/data")
@RequiredArgsConstructor
public class DataSyncController {

    private final DataSyncService dataSyncService;

    @GetMapping
    public Mono<Void> syncData() {
        // TODO: Implement this method
        return null;
    }

}
