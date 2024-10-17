package es.in2.desmos.domain.services.api.impl;

import es.in2.desmos.domain.exceptions.UnauthorizedDomeParticipantException;
import es.in2.desmos.domain.repositories.TrustedAccessNodesListRepository;
import es.in2.desmos.domain.services.api.DomeParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomeParticipantServiceImpl implements DomeParticipantService {

    private final TrustedAccessNodesListRepository trustedAccessNodesListRepository;

    @Override
    public Mono<Void> validateDomeParticipant(String processId, String dltAddress) {
        log.info("ProcessID: {} - Validating Dome Participant: {}", processId, dltAddress);
        return trustedAccessNodesListRepository.existsDltAddressByValue(Mono.just(dltAddress))
                .switchIfEmpty(Mono.error(new UnauthorizedDomeParticipantException("Dome Participant not found because empty")))
                .flatMap(exists ->
                        Boolean.TRUE.equals(exists) ?
                                Mono.empty() :
                                Mono.error(new UnauthorizedDomeParticipantException("Dome Participant not found")));
    }
}