package es.in2.desmos.domain.services.api.impl;

import es.in2.desmos.domain.exceptions.UnauthorizedDomeParticipantException;
import es.in2.desmos.domain.repositories.TrustedAccessNodesListRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DomeParticipantServiceTest {
    @Mock
    private TrustedAccessNodesListRepository domeParticipantRepository;

    @InjectMocks
    private DomeParticipantServiceImpl domeParticipantService;

    private static final String VALID_DLT_ADDRESS = "0xValidAddress";
    private static final String INVALID_DLT_ADDRESS = "0xInvalidAddress";
    private static final String PROCESS_ID = "process123";

    @Test
    void itShouldReturnDomeParticipantWhenParticipantIsFound() {
        when(domeParticipantRepository.existsDltAddressByValue(any()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(domeParticipantService.validateDomeParticipant(PROCESS_ID, VALID_DLT_ADDRESS))
                .verifyComplete();
    }

    @Test
    void itShouldThrowUnauthorizedDomeParticipantExceptionWhenParticipantIsNotFound() {
        when(domeParticipantRepository.existsDltAddressByValue(any()))
                .thenReturn(Mono.just(false));

        StepVerifier.create(domeParticipantService.validateDomeParticipant(PROCESS_ID, INVALID_DLT_ADDRESS))
                .consumeErrorWith(error -> assertThat(error).isInstanceOf(UnauthorizedDomeParticipantException.class))
                .verify();
    }

    @Test
    void itShouldThrowUnauthorizedDomeParticipantExceptionWhenParticipantIsNotFoundBecauseEmpty() {
        when(domeParticipantRepository.existsDltAddressByValue(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(domeParticipantService.validateDomeParticipant(PROCESS_ID, INVALID_DLT_ADDRESS))
                .consumeErrorWith(error -> assertThat(error).isInstanceOf(UnauthorizedDomeParticipantException.class))
                .verify();
    }
}