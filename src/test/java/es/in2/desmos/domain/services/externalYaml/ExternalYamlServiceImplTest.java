//package es.in2.desmos.domain.services.externalYaml;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
//import es.in2.desmos.domain.models.AccessNodeYamlData;
//import es.in2.desmos.domain.services.sync.services.impl.ExternalYamlServiceImpl;
//import es.in2.desmos.infrastructure.configs.ApiConfig;
//import es.in2.desmos.infrastructure.configs.cache.AccessNodeMemoryStore;
//import es.in2.desmos.infrastructure.configs.properties.AccessNodeProperties;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.core.env.Environment;
//import org.springframework.http.MediaType;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class ExternalYamlServiceImplTest {
//
//    @Mock
//    private ApiConfig apiConfig;
//
//    @Mock
//    private WebClient webClientMock;
//
//    @Mock
//    WebClient.RequestHeadersUriSpec webClientRequestHeadersUriSpecMock;
//
//    @Mock
//    WebClient.RequestHeadersSpec webClientRequestHeadersSpecMock;
//
//    @Mock
//    WebClient.ResponseSpec webClientResponseSpecMock;
//
//    @Mock
//    private AccessNodeProperties accessNodeProperties;
//
//    @Mock
//    private Environment env;
//
//    @Mock
//    private AccessNodeMemoryStore accessNodeMemoryStore;
//
//    @InjectMocks
//    private ExternalYamlServiceImpl externalYamlService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testGetAccessNodeYamlDataFromExternalSource_Success() {
//        String repoPath = "repoPath";
//        //Arrange
//        when(accessNodeProperties.trustedAccessNodesList()).thenReturn("/prefix/directory");
//        //when(env.getActiveProfiles()).thenReturn(new String[]{"dev"});
//        when(apiConfig.getCurrentEnvironment())
//                .thenReturn("dev");
//        when(apiConfig.webClient()).thenReturn(webClientMock);
//        when(webClientMock.get()).thenReturn(webClientRequestHeadersUriSpecMock);
//        when(webClientRequestHeadersUriSpecMock.uri(repoPath)).thenReturn(webClientRequestHeadersSpecMock);
//        when(webClientRequestHeadersSpecMock.accept(any(MediaType.class))).thenReturn(webClientRequestHeadersSpecMock);
//        when(webClientRequestHeadersSpecMock.header(anyString(), anyString())).thenReturn(webClientRequestHeadersSpecMock);
//        when(webClientRequestHeadersSpecMock.retrieve()).thenReturn(webClientResponseSpecMock);
//        when(webClientResponseSpecMock.onStatus(any(), any())).thenReturn(webClientResponseSpecMock);
//        when(webClientResponseSpecMock.bodyToMono(String.class)).thenReturn(Mono.empty());
//
//
//        //Act & Assert
//        StepVerifier.create(externalYamlService.getAccessNodeYamlDataFromExternalSource("processId"))
//                .assertNext(response -> {
//                })
//                .verifyComplete();
//
//    }
//}
