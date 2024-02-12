package es.in2.desmos.api.service;

import es.in2.desmos.api.model.EventQueue;
import es.in2.desmos.api.service.impl.QueueServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.util.concurrent.PriorityBlockingQueue;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueServiceImplTest {

    @Mock
    private Sinks.Many<EventQueue> mockSink;

    @InjectMocks
    private QueueServiceImpl queueService;

    @Mock
    private PriorityBlockingQueue<EventQueue> mockQueue;

    @Test
    void enqueueEventTest() {
        EventQueue event = new EventQueue(); // Suponiendo que EventQueue es tu clase de evento

        // Realizar el test
        StepVerifier.create(queueService.enqueueEvent(event))
                .verifyComplete();



    }
}
