package es.in2.desmos.domain.services.api.impl;

import es.in2.desmos.domain.exceptions.UnauthorizedDomeParticipantException;
import es.in2.desmos.domain.models.DomeParticipant;
import es.in2.desmos.domain.repositories.DomeParticipantRepository;
import es.in2.desmos.domain.services.api.DomeParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomeParticipantServiceImpl implements DomeParticipantService {

    private final DomeParticipantRepository domeParticipantRepository;

    @Override
    public Mono<DomeParticipant> validateDomeParticipant(String processId, String ethereumAddress) {
        log.info("ProcessID: {} - Validating Dome Participant: {}", processId, ethereumAddress);
        return domeParticipantRepository.findByEthereumAddress(ethereumAddress)
                .switchIfEmpty(Mono.error(new UnauthorizedDomeParticipantException("Dome Participant not found")));
    }

}
