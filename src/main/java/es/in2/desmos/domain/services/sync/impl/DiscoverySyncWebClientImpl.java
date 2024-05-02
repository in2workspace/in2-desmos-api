package es.in2.desmos.domain.services.sync.impl;

import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.sync.DiscoverySyncWebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscoverySyncWebClientImpl implements DiscoverySyncWebClient {
    @Override
    public Mono<List<MVEntity4DataNegotiation>> makeRequest(String processId, Mono<String> externalAccessNodeMono, Mono<List<MVEntity4DataNegotiation>> localMvEntities4DataNegotiationMono) {
        return null;
    }
}
