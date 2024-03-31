package es.in2.desmos.domain.services.sync.impl;

import es.in2.desmos.domain.services.sync.DataSyncService;
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
