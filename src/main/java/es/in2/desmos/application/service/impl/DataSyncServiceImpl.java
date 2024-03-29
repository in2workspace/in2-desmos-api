package es.in2.desmos.application.service.impl;

import es.in2.desmos.application.service.DataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSyncServiceImpl implements DataSyncService {

    @Override
    public Mono<Void> synchronizeData(String processId) {
        log.debug("ProcessID: {} - Synchronizing data...", processId);
        return Mono.empty();
    }

}
