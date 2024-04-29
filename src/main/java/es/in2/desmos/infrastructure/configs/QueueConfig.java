package es.in2.desmos.infrastructure.configs;

import es.in2.desmos.domain.services.api.QueueService;
import es.in2.desmos.domain.services.api.impl.QueueServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

    @Bean
    public QueueService pendingPublishEventsQueue() {
        return new QueueServiceImpl();
    }

    @Bean
    public QueueService pendingSubscribeEventsQueue() {
        return new QueueServiceImpl();
    }

}
