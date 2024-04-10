package es.in2.desmos.domain.events;

import es.in2.desmos.domain.services.sync.NewEntitiesCreatorService;
import es.in2.desmos.objectmothers.ProductOfferingMother;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
class EntitiesCreatorEventTest {

    @InjectMocks
    private EntitiesCreatorEventPublisher entitiesCreatorEventPublisher;

    @MockBean
    private NewEntitiesCreatorService newEntitiesCreatorService;

    @Test
    void itShouldCreateNewEntitiesWhenPublishService() {
        Mono<String> issuer = Mono.just("https://example.org");
        Mono<List<String>> externalEntityIds = createExternalEntityIdList();
        Mono<List<String>> internalEntityIds = createInternalEntityIds();

        entitiesCreatorEventPublisher.publishEvent(issuer, externalEntityIds, internalEntityIds);

        verify(newEntitiesCreatorService.addNewEntities(any(), any(), any())).thenReturn(Mono.empty());
    }

    private static @NotNull Mono<List<String>> createInternalEntityIds() {
        List<String> internalEntitiesIds = new ArrayList<>();
        internalEntitiesIds.add(ProductOfferingMother.sample3().id());
        internalEntitiesIds.add(ProductOfferingMother.sample4().id());
        return Mono.just(internalEntitiesIds);
    }

    private static @NotNull Mono<List<String>> createExternalEntityIdList() {
        List<String> externalEntitiesIdList = new ArrayList<>();
        externalEntitiesIdList.add(ProductOfferingMother.sample1().id());
        externalEntitiesIdList.add(ProductOfferingMother.sample2().id());
        return Mono.just(externalEntitiesIdList);
    }
}
