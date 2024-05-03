package es.in2.desmos.infrastructure.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.DiscoverySyncRequest;
import es.in2.desmos.domain.models.DiscoverySyncResponse;
import es.in2.desmos.domain.models.Id;
import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
import es.in2.desmos.domain.services.sync.jobs.P2PDataSyncJob;
import es.in2.desmos.domain.services.sync.services.DataSyncService;
import es.in2.desmos.objectmothers.BrokerDataMother;
import es.in2.desmos.objectmothers.DiscoverySyncRequestMother;
import es.in2.desmos.objectmothers.DiscoverySyncResponseMother;
import es.in2.desmos.objectmothers.IdMother;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@WebFluxTest(DataSyncController.class)
class DataSyncControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    // todo: move to configuration
    @Value("${broker.externalDomain}")
    private String contextBrokerExternalDomain;

    @MockBean
    private P2PDataSyncJob p2PDataSyncJob;

    @MockBean
    private DataSyncService dataSyncService;

    @Test
    void testSyncData() {
        Mockito.when(dataSyncService.synchronizeData(anyString())).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/sync/p2p/data")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentLength(-1);
    }

    @Test
    void itShouldReturnEntityWithIssuer() throws JsonProcessingException {

        Mono<DiscoverySyncRequest> discoverySyncRequest = Mono.just(DiscoverySyncRequestMother.list1And2());

        DiscoverySyncResponse discoverySyncResponse = DiscoverySyncResponseMother.list3And4(contextBrokerExternalDomain);
        var discoverySyncResponseJson = objectMapper.writeValueAsString(discoverySyncResponse);

        Mono<List<MVEntity4DataNegotiation>> localEntityIds = Mono.just(DiscoverySyncResponseMother.list3And4(contextBrokerExternalDomain).entities());
        when(p2PDataSyncJob.dataDiscovery(anyString(), any(), any())).thenReturn(localEntityIds);

        webTestClient.post()
                .uri("/api/v1/sync/p2p/discovery")
                .contentType(MediaType.APPLICATION_JSON)
                .body(discoverySyncRequest, DiscoverySyncRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(discoverySyncResponseJson)
                .consumeWith(System.out::println);

        verify(p2PDataSyncJob, times(1)).dataDiscovery(anyString(), any(), any());
        verifyNoMoreInteractions(p2PDataSyncJob);
    }

    @Test
    void itShouldReturnAllRequestedEntities() throws JSONException {
        Id[] entitySyncRequest = IdMother.entitiesRequest;
        Mono<Id[]> entitySyncRequestMono = Mono.just(entitySyncRequest);

        String expectedResponse = BrokerDataMother.getEntityRequestBrokerJson;

        JSONArray expectedResponseJsonArray = new JSONArray(expectedResponse);
        List<String> localEntities = new ArrayList<>();
        for (int i = 0; i < expectedResponseJsonArray.length(); i++) {
            String entity = expectedResponseJsonArray.getString(i);
            localEntities.add(entity);
        }
        Mono<List<String>> localEntitiesMono = Mono.just(localEntities);

        when(p2PDataSyncJob.getLocalEntitiesById(any(), any())).thenReturn(localEntitiesMono);

        webTestClient.post()
                .uri("/api/v1/sync/p2p/entities")
                .contentType(MediaType.APPLICATION_JSON)
                .body(entitySyncRequestMono, Id[].class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(expectedResponse)
                .consumeWith(System.out::println);

        verify(p2PDataSyncJob, times(1)).getLocalEntitiesById(any(), any());
        verifyNoMoreInteractions(p2PDataSyncJob);
    }

}
