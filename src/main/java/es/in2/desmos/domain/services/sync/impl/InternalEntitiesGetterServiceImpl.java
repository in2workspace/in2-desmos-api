package es.in2.desmos.domain.services.sync.impl;

import es.in2.desmos.domain.models.ProductOffering;
import es.in2.desmos.domain.services.sync.InternalEntitiesGetterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalEntitiesGetterServiceImpl implements InternalEntitiesGetterService {
    @Override
    public Mono<List<ProductOffering>> getInternalEntityIds() {
        // TODO
        return null;
    }
}
