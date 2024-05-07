package es.in2.desmos.domain.services.blockchain.adapter.impl;

import es.in2.desmos.domain.models.BlockchainTxPayload;
import es.in2.desmos.domain.services.blockchain.adapter.BlockchainAdapterService;
import es.in2.desmos.domain.services.blockchain.adapter.factory.BlockchainAdapterFactory;
import es.in2.desmos.domain.services.blockchain.impl.BlockchainPublisherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlockchainPublisherServiceTests {

    private final String eventType = "ProductOffering";
    private final String organizationIdentifier = "0x40b0ab9dfd960064fb7e9fdf77f889c71569e349055ff563e8d699d8fa97fa90";
    private final String entityId = "0x6f6468ded8276d009ab1b6c578c2b922053acd6b5a507f36d408d3f7c9ae91d0";
    private final String previousEntityHash = "0x98d9658d98764dbe135b316f52a98116b4b02f9d7e57212aa86335c42a58539a";
    private final String dataLocation = "http://localhost:8080/ngsi-ld/v1/entities/" +
            "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69" +
            "?hl=03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
    private final List<String> metadata = List.of("0xdd98910dbc7831753bab3da302ce5bf9d73ac13961913c2e774de8e737867f0d",
            "0x947cccb1a978e374a4b36550389768d405bf5b81817175ab9023b5e3d96ab966");

    BlockchainTxPayload blockchainTxPayload = BlockchainTxPayload.builder()
            .eventType(eventType)
            .organizationIdentifier(organizationIdentifier)
            .entityId(entityId)
            .previousEntityHash(previousEntityHash)
            .dataLocation(dataLocation)
            .metadata(metadata)
            .build();

    @Mock
    private BlockchainAdapterFactory blockchainAdapterFactory;

    @Mock
    private BlockchainAdapterService blockchainAdapterService;

    private BlockchainPublisherServiceImpl blockchainPublisherService;

    @BeforeEach
    void init() {
        when(blockchainAdapterFactory.getBlockchainAdapter()).thenReturn(blockchainAdapterService);
        blockchainPublisherService = new BlockchainPublisherServiceImpl(blockchainAdapterFactory);
    }

    @Test
    void publishDataToBlockchain() {
        //Arrange
        String processId = "processId";
        //Act
        when(blockchainAdapterService.postTxPayload(processId, blockchainTxPayload)).thenReturn(Mono.empty());
        //Assert
        StepVerifier.create(blockchainPublisherService.publishDataToBlockchain(processId, blockchainTxPayload))
                .verifyComplete();
    }
}