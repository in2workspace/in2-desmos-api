package es.in2.desmos.testsbase;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;

public abstract class MockCorsTrustedAccessNodesListServerBase {
    private static MockWebServer mockWebServer;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        mockWebServer.setDispatcher(trustedAccessNodesListServerDispatcher());
    }

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("access-node.trustedAccessNodesList", () -> mockWebServer.url("").toString());
    }


    @AfterAll
    static void afterAll() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    private static Dispatcher trustedAccessNodesListServerDispatcher() {
        return new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) {
                return new MockResponse()
                        .setBody("""
                                organizations:
                                  - name: DOME
                                    publicKey: 0x0486573f96a9e5a0007855cba27af53d2d73d69cc143266bc336e361d2f5124f6639c813e62a1c8642132de455b72d65c620f18d69c09e30123d420fcb85de361d
                                    url: https://desmos.dome-marketplace-sbx.org
                                    dltAddress: 0x40b0ab9dfd960064fb7e9fdf77f889c71569e349055ff563e8d699d8fa97fa90""")
                        .addHeader("Content-Type", "application/json");
            }
        };
    }
}
