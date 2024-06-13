package es.in2.desmos.domain.services.api;

import es.in2.desmos.domain.models.DomeParticipant;
import reactor.core.publisher.Mono;

public interface DomeParticipantService {
    Mono<DomeParticipant> validateDomeParticipant(String processId, String domeParticipantId);
}
