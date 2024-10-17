package es.in2.desmos.domain.services.api;

import reactor.core.publisher.Mono;

public interface DomeParticipantService {
    Mono<Void> validateDomeParticipant(String processId, String domeParticipantId);
}
