package es.in2.desmos.application.schedulers;

import es.in2.desmos.domain.services.sync.services.ExternalYamlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccessNodeScheduler {

    private final ExternalYamlService externalYamlService;

    @Scheduled(cron = "0 0 3 * * *")
    public void getPublicKeyFromAccessNodeRepository() {
        String processId = UUID.randomUUID().toString();

        log.info("ProcessID: {} - Starting cron Access Node Schedule cron job...", processId);

        externalYamlService.getAccessNodeYamlDataFromExternalSource(processId).subscribe();
    }
}
