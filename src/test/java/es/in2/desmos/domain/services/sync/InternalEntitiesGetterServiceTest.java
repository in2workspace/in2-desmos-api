package es.in2.desmos.domain.services.sync;

import es.in2.desmos.domain.models.ProductOffering;
import es.in2.desmos.domain.services.sync.impl.InternalEntitiesGetterServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class InternalEntitiesGetterServiceTest {
    @InjectMocks
    private InternalEntitiesGetterServiceImpl internalEntitiesGetterService;

    @Test
    void itShouldReturnInternalEntities() {
        Mono<List<ProductOffering>> internalEntitiesIds = internalEntitiesGetterService.getInternalEntityIds();


    }
}
