//package es.in2.desmos.infrastructure.controllers;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.nimbusds.jose.JOSEException;
//import es.in2.desmos.domain.models.DiscoverySyncRequest;
//import es.in2.desmos.domain.models.DiscoverySyncResponse;
//import es.in2.desmos.domain.models.Id;
//import es.in2.desmos.domain.models.MVEntity4DataNegotiation;
//import es.in2.desmos.application.workflows.jobs.P2PDataSyncJob;
//import es.in2.desmos.domain.services.sync.services.DataSyncService;
//import es.in2.desmos.infrastructure.configs.ApiConfig;
//import es.in2.desmos.infrastructure.configs.BrokerConfig;
//import es.in2.desmos.infrastructure.security.JwtTokenProvider;
//import es.in2.desmos.infrastructure.security.SecurityProperties;
//import es.in2.desmos.it.ContainerManager;
//import es.in2.desmos.objectmothers.BrokerDataMother;
//import es.in2.desmos.objectmothers.DiscoverySyncRequestMother;
//import es.in2.desmos.objectmothers.DiscoverySyncResponseMother;
//import es.in2.desmos.objectmothers.IdMother;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import reactor.core.publisher.Mono;
//
//import java.lang.reflect.Field;
//import java.security.NoSuchAlgorithmException;
//import java.security.NoSuchProviderException;
//import java.security.spec.InvalidKeySpecException;
//import java.util.ArrayList;
//import java.util.Base64;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@WebFluxTest(DataSyncController.class)
//class DataSyncControllerTests {
//
//    @Autowired
//    private WebTestClient webTestClient;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private P2PDataSyncJob p2PDataSyncJob;
//
//    @MockBean
//    private DataSyncService dataSyncService;
//
//    @MockBean
//    private BrokerConfig brokerConfig;
//
//    @MockBean
//    private ApiConfig apiConfig;
//
//    @MockBean
//    private JwtTokenProvider jwtTokenProvider;
//
//    @Test
//    void testSyncData() throws JOSEException {
//        Mockito.when(dataSyncService.synchronizeData(anyString())).thenReturn(Mono.empty());
//        Mockito.when(jwtTokenProvider.generateToken(anyString())).thenReturn("eyJ0eXAiOiJkcG9wK2p3dCIsImFsZyI6IkVTMjU2SyIsImp3ayI6eyJrdHkiOiJFQyIsInVzZSI6InNpZyIsImNydiI6InNlY3AyNTZrMSIsIngiOiJobGNfbHFubG9BQjRWY3VpZXZVOUxYUFduTUZESm12RE51TmgwdlVTVDJZIiwieSI6Ik9jZ1Q1aW9jaGtJVExlUlZ0eTFseGlEeGpXbkFuakFTUFVJUHk0WGVOaDAifX0.eyJodG0iOiJQT1NUIiwiaHR1IjoiIiwiaWF0IjoxNzIzNTM2NDgwLCJqdGkiOiIyMDU0ZTYwNC04YzAzLTQ2ZjItYWY5NC05M2IxODVhYWU5MTYifQ.dOFfCTWH8qPWdj2Y-JdnXl5uOBSJvCJlHtCW4ENxTTVtrytkIK_sIx8jFLPAapTcYsAOdx_IYMxDeiI-kZKdBA");
//
//        String token;
//        try {
//            token = jwtTokenProvider.generateToken("/api/v1/sync/p2p/data");
//        } catch (JOSEException e) {
//            throw new RuntimeException(e);
//        }
//
//        webTestClient.get()
//                .uri("/api/v1/sync/p2p/data")
//                .header("Authorization", "Bearer " + token)
//                .exchange()
//                .expectStatus().isOk();
//    }
//
//    @Test
//    void itShouldReturnEntityWithIssuer() throws JsonProcessingException {
//        Mono<DiscoverySyncRequest> discoverySyncRequest = Mono.just(DiscoverySyncRequestMother.list1And2());
//        var contextBrokerExternalDomain = "http://example.org";
//        DiscoverySyncResponse discoverySyncResponse = DiscoverySyncResponseMother.list3And4(contextBrokerExternalDomain);
//        var discoverySyncResponseJson = objectMapper.writeValueAsString(discoverySyncResponse);
//
//        Mono<List<MVEntity4DataNegotiation>> localEntityIds = Mono.just(DiscoverySyncResponseMother.list3And4(contextBrokerExternalDomain).entities());
//        when(p2PDataSyncJob.dataDiscovery(anyString(), any(), any())).thenReturn(localEntityIds);
//
//        when(apiConfig.getExternalDomain()).thenReturn(contextBrokerExternalDomain);
//
//        webTestClient.post()
//                .uri("/api/v1/sync/p2p/discovery")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(discoverySyncRequest, DiscoverySyncRequest.class)
//                .exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectBody()
//                .json(discoverySyncResponseJson)
//                .consumeWith(System.out::println);
//
//        verify(p2PDataSyncJob, times(1)).dataDiscovery(anyString(), any(), any());
//        verifyNoMoreInteractions(p2PDataSyncJob);
//    }
//
//    @Test
//    void itShouldReturnAllRequestedEntities() throws JSONException {
//        Id[] entitySyncRequest = IdMother.entitiesRequest;
//        Mono<Id[]> entitySyncRequestMono = Mono.just(entitySyncRequest);
//
//        String entities = BrokerDataMother.getEntityRequestBrokerJson;
//
//        JSONArray originalEntities = new JSONArray(entities);
//        JSONArray expectedEntities = new JSONArray();
//        List<String> base64Entities = new ArrayList<>();
//        for (int i = 0; i < originalEntities.length(); i++) {
//            String entity = originalEntities.getString(i);
//            String encodedEntity = Base64.getEncoder().encodeToString(entity.getBytes());
//            base64Entities.add(encodedEntity);
//            expectedEntities.put(encodedEntity);
//        }
//
//
//        when(p2PDataSyncJob.getLocalEntitiesByIdInBase64(any(), any())).thenReturn(Mono.just(base64Entities));
//
//        webTestClient.post()
//                .uri("/api/v1/sync/p2p/entities")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(entitySyncRequestMono, Id[].class)
//                .exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectBody()
//                .json(expectedEntities.toString())
//                .consumeWith(System.out::println);
//
//        verify(p2PDataSyncJob, times(1)).getLocalEntitiesByIdInBase64(any(), any());
//        verifyNoMoreInteractions(p2PDataSyncJob);
//    }
//
//}
