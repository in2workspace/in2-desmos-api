package es.in2.desmos.domain.services.api;

import es.in2.desmos.application.workflows.PublishWorkflow;
import es.in2.desmos.application.workflows.SubscribeWorkflow;
import es.in2.desmos.domain.services.api.impl.SubscriptionManagerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionManagerServiceTest {

    @Mock
    private PublishWorkflow publishWorkflow;

    @Mock
    private SubscribeWorkflow subscribeWorkflow;

    @Mock
    private Disposable publishQueueDisposable;

    @Mock
    private Disposable subscribeQueueDisposable;

    @InjectMocks
    private SubscriptionManagerServiceImpl subscriptionManagerService;

    @Test
    void testStartPublishSubscriptionWhenNotRunning() {
        String processId = "test-process-id";
        when(publishWorkflow.startPublishWorkflow(processId)).thenReturn(Flux.empty());

        subscriptionManagerService.startPublishSubscription(processId);

        verify(publishWorkflow).startPublishWorkflow(processId);
    }

    @Test
    void testStopPublishSubscriptionWhenNotRunning() {
        String processId = "test-process-id";

        subscriptionManagerService.stopPublishSubscription(processId);

        verify(publishQueueDisposable, never()).dispose();
    }

    @Test
    void testStartSubscribeSubscriptionWhenNotRunning() {
        String processId = "test-process-id";
        when(subscribeWorkflow.startSubscribeWorkflow(processId)).thenReturn(Flux.empty());

        subscriptionManagerService.startSubscribeSubscription(processId);

        verify(subscribeWorkflow).startSubscribeWorkflow(processId);
        // Add additional verifications for logging if needed
    }

    @Test
    void testStopSubscribeSubscriptionWhenNotRunning() {
        String processId = "test-process-id";

        subscriptionManagerService.stopSubscribeSubscription(processId);

        verify(subscribeQueueDisposable, never()).dispose();
    }
}