package es.in2.desmos.domain.services.sync.jobs;

import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import reactor.core.publisher.Mono;

import java.util.List;

public interface P2PDataSyncJob {
    Mono<Void> synchronizeData(String processId);

    Mono<List<MVEntity4DataNegotiation>> dataDiscovery(String processId, Mono<String> issuer, Mono<List<MVEntity4DataNegotiation>> externalMvEntities4DataNegotiation);

    Mono<List<String>> getLocalEntitiesById(String processId, Mono<List<Id>> ids);
}
