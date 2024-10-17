package es.in2.desmos.it.domain.services;

import es.in2.desmos.application.workflows.jobs.DataNegotiationJob;
import es.in2.desmos.domain.events.DataNegotiationEventPublisher;
import es.in2.desmos.it.ContainerManager;
import es.in2.desmos.objectmothers.DataNegotiationEventMother;
import es.in2.desmos.testsbase.MockCorsTrustedAccessNodesListServerBase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.Mockito.*;


@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DataNegotiationJobIT {
    @Autowired
    private DataNegotiationEventPublisher dataNegotiationEventPublisher;

    @SpyBean
    private DataNegotiationJob dataNegotiationJob;

    @DynamicPropertySource
    private static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ContainerManager.postgresqlProperties(registry);
    }

    @Test
    void itShouldBeListenWhenEventIsCalled() {
        dataNegotiationEventPublisher.publishEvent(DataNegotiationEventMother.empty());

        verify(dataNegotiationJob, timeout(500).times(1)).negotiateDataSyncFromEvent(any());
    }
}
