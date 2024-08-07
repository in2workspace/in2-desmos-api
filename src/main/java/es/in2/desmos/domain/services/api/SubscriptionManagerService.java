package es.in2.desmos.domain.services.api;

public interface SubscriptionManagerService {
    void startPublishSubscription(String processId);

    void stopPublishSubscription(String processId);

    void restartPublishSubscription(String processId);

    void startSubscribeSubscription(String processId);

    void stopSubscribeSubscription(String processId);

    void restartSubscribeSubscription(String processId);
}
