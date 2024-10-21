package es.in2.desmos.application.schedulers;

import es.in2.desmos.application.workflows.DataSyncWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSyncScheduler {

    private final DataSyncWorkflow dataSyncWorkflow;

    @Scheduled(cron = "0 0 2 * * *")
    public Flux<Void> initializeDataSync() {
        String processId = UUID.randomUUID().toString();

        log.info("ProcessID: {} - Starting cron Data Sync Workflow cron job...", processId);

        return dataSyncWorkflow.startDataSyncWorkflow(processId);
    }
}
