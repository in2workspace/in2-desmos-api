package es.in2.desmos.domain.services.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.BlockchainNotification;
import es.in2.desmos.domain.services.api.RecoverRepositoryService;
import es.in2.desmos.domain.services.broker.adapter.impl.ScorpioAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScorpioAdapterTests {
    @Mock
    private RecoverRepositoryService recoverRepositoryService;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ScorpioAdapter scorpioAdapter;

    private BlockchainNotification blockchainNotification;

    @BeforeEach
    void setUp() {
        blockchainNotification = BlockchainNotification.builder()
                .id(123123)
                .publisherAddress("https://example.com")
                .dataLocation("dataLocation")
                .eventType("Event-Type")
                .build();
    }

    @Test
    void testRecoverProcessesCorrectInformation() {
        // Arrange
        when(recoverRepositoryService.saveBlockchainNotificationRecover(any(), any())).thenReturn(Mono.empty());

        // Act
        String processId = "test-process-id";
        StepVerifier.create(scorpioAdapter.recover(processId, blockchainNotification))
                .verifyComplete();

        // Assert
        verify(recoverRepositoryService).saveBlockchainNotificationRecover(eq(processId), argThat(recover ->
                recover.getEventType().equals("Event-Type") &&
                        recover.getPublisherAddress().equals("https://example.com")
        ));
    }
}
