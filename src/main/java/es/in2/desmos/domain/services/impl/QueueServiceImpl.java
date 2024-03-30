package es.in2.desmos.domain.services.impl;

import es.in2.desmos.domain.models.EventQueue;
import es.in2.desmos.domain.services.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.PriorityBlockingQueue;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueServiceImpl implements QueueService {

    private final Sinks.Many<EventQueue> sink = Sinks.many().multicast().onBackpressureBuffer();
    private final PriorityBlockingQueue<EventQueue> queue = new PriorityBlockingQueue<>();

    @Override
    public Mono<Void> enqueueEvent(EventQueue event) {
        if (queue.offer(event)) {
            log.debug(queue.toString());
            emitNext();
            return Mono.empty();
        }
        return Mono.empty();
    }

    private void emitNext() {
        EventQueue eventQueue = queue.poll();
        if (eventQueue != null) {
            log.debug(eventQueue.toString());
            sink.tryEmitNext(eventQueue);
        }
    }

    @Override
    public Flux<EventQueue> getEventStream() {
        return sink.asFlux();
    }

}
