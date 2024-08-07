package es.in2.desmos.domain.services.api.impl;

import es.in2.desmos.application.workflows.PublishWorkflow;
import es.in2.desmos.application.workflows.SubscribeWorkflow;
import es.in2.desmos.domain.services.api.SubscriptionManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionManagerServiceImpl implements SubscriptionManagerService {

    private final PublishWorkflow publishWorkflow;
    private final SubscribeWorkflow subscribeWorkflow;
    private Disposable publishQueueDisposable;
    private Disposable subscribeQueueDisposable;

    @Override
    public void startPublishSubscription(String processId) {
        if (publishQueueDisposable == null || publishQueueDisposable.isDisposed()) {
            publishQueueDisposable = publishWorkflow.startPublishWorkflow(processId)
                    .subscribe(
                            null,
                            error -> log.error("ProcessID: {} - Error occurred during Publish Workflow", processId, error),
                            () -> log.info("ProcessID: {} - Publish Workflow completed", processId)
                    );
        } else {
            log.info("Publish Workflow is already running.");
        }
    }

    @Override
    public void stopPublishSubscription(String processId) {
        if (publishQueueDisposable != null && !publishQueueDisposable.isDisposed()) {
            publishQueueDisposable.dispose();
            log.info("ProcessID: {} - Publish Workflow has been stopped.", processId);
        } else {
            log.info("ProcessID: {} - Publish Workflow is not running.", processId);
        }
    }

    @Override
    public void restartPublishSubscription(String processId) {
        stopPublishSubscription(processId);
        startPublishSubscription(processId);
    }

    @Override
    public void startSubscribeSubscription(String processId) {
        if (subscribeQueueDisposable == null || subscribeQueueDisposable.isDisposed()) {
            subscribeQueueDisposable = subscribeWorkflow.startSubscribeWorkflow(processId)
                    .subscribe(
                            null,
                            error -> log.error("ProcessID: {} - Error occurred during Subscribe Workflow", processId, error),
                            () -> log.info("ProcessID: {} - Subscribe Workflow completed", processId)
                    );
        } else {
            log.info("ProcessID: {} - Subscribe Workflow is already running.", processId);
        }
    }

    @Override
    public void stopSubscribeSubscription(String processId) {
        if (subscribeQueueDisposable != null && !subscribeQueueDisposable.isDisposed()) {
            subscribeQueueDisposable.dispose();
            log.info("ProcessID: {} - Subscribe Workflow has been stopped.", processId);
        } else {
            log.info("ProcessID: {} - Subscribe Workflow is not running.", processId);
        }
    }

    @Override
    public void restartSubscribeSubscription(String processId) {
        stopSubscribeSubscription(processId);
        startSubscribeSubscription(processId);
    }
}
