package es.in2.desmos.domain.services.policies;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.policies.impl.ReplicationPoliciesServiceImpl;
import es.in2.desmos.objectmothers.MVEntity4DataNegotiationMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ReplicationPoliciesServiceTest {

    @InjectMocks
    private ReplicationPoliciesServiceImpl replicationPoliciesService;

    @Test
    void itShouldBeReplicableWhenAllFieldsAreValid() {
        String processId = "process-123";
        MVEntity4DataNegotiation mvEntity = MVEntity4DataNegotiationMother.replicationValidFields();

        Mono<Boolean> result = replicationPoliciesService.isMVEntityReplicable(processId, mvEntity);

        StepVerifier.create(result)
                .assertNext(isReplicable -> assertThat(isReplicable).isTrue())
                .verifyComplete();
    }

    @Test
    void itShouldBeReplicableWhenStartDateTimeIsNullAndOtherFieldsAreValid() {
        String processId = "process-123";
        MVEntity4DataNegotiation mvEntity = MVEntity4DataNegotiationMother.replicationValidFieldsAndNullStartDateTime();

        Mono<Boolean> result = replicationPoliciesService.isMVEntityReplicable(processId, mvEntity);

        StepVerifier.create(result)
                .assertNext(isReplicable -> assertThat(isReplicable).isTrue())
                .verifyComplete();
    }

    @Test
    void itShouldBeReplicableWhenEndDateTimeIsNullAndOtherFieldsAreValid() {
        String processId = "process-123";
        MVEntity4DataNegotiation mvEntity = MVEntity4DataNegotiationMother.replicationValidFieldsAndNullEndDateTime();

        Mono<Boolean> result = replicationPoliciesService.isMVEntityReplicable(processId, mvEntity);

        StepVerifier.create(result)
                .assertNext(isReplicable -> assertThat(isReplicable).isTrue())
                .verifyComplete();
    }

    @Test
    void itShouldBeNotReplicableWhenLifecycleStatusIsInvalid() {
        String processId = "process-123";
        MVEntity4DataNegotiation mvEntity = MVEntity4DataNegotiationMother.replicationInvalidLifecycleStatus();

        Mono<Boolean> result = replicationPoliciesService.isMVEntityReplicable(processId, mvEntity);

        StepVerifier.create(result)
                .assertNext(isReplicable -> assertThat(isReplicable).isFalse())
                .verifyComplete();
    }

    @Test
    void itShouldBeNotReplicableWhenStartDateTimeIsInTheFuture() {
        String processId = "process-123";
        MVEntity4DataNegotiation mvEntity = MVEntity4DataNegotiationMother.replicationInvalidFutureStartDateTime();


        Mono<Boolean> result = replicationPoliciesService.isMVEntityReplicable(processId, mvEntity);

        StepVerifier.create(result)
                .assertNext(isReplicable -> assertThat(isReplicable).isFalse())
                .verifyComplete();
    }

    @Test
    void itShouldBeNotReplicableWhenEndDateTimeIsInThePast() {
        String processId = "process-123";
        MVEntity4DataNegotiation mvEntity = MVEntity4DataNegotiationMother.replicationInvalidPastEndDateTime();

        Mono<Boolean> result = replicationPoliciesService.isMVEntityReplicable(processId, mvEntity);

        StepVerifier.create(result)
                .assertNext(isReplicable -> assertThat(isReplicable).isFalse())
                .verifyComplete();
    }
}