package es.in2.desmos.infrastructure.configs;

import es.in2.desmos.domain.service.QueueService;
import es.in2.desmos.domain.service.impl.QueueServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

    @Bean
    public QueueService dataPublicationQueue() {
        return new QueueServiceImpl();
    }

    @Bean
    public QueueService dataRetrievalQueue() {
        return new QueueServiceImpl();
    }

}
