package es.in2.desmos;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class ContainerManager {

    private static final ContainerManager INSTANCE = new ContainerManager();

    private static final Network testNetwork = Network.newNetwork();
    private static final PostgreSQLContainer<?> postgresContainer;
    private static final GenericContainer<?> postgisContainer;
    private static final GenericContainer<?> scorpioContainer;
    private static final GenericContainer<?> blockchainAdapterContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("desmos")
                .withUsername("guest")
                .withPassword("guest")
                .withNetwork(testNetwork)
                .withNetworkAliases("postgres");
        postgresContainer.start();

        postgisContainer = new GenericContainer<>(DockerImageName.parse("postgis/postgis"))
                .withExposedPorts(5432)
                .withEnv("POSTGRES_USER", "ngb")
                .withEnv("POSTGRES_PASSWORD", "ngb")
                .withEnv("POSTGRES_DB", "ngb")
                .withNetwork(testNetwork)
                .withNetworkAliases("postgis");
        postgisContainer.start();

        scorpioContainer = new GenericContainer<>(DockerImageName.parse("scorpiobroker/all-in-one-runner:java-latest"))
                .withExposedPorts(9090)
                .withEnv("DBHOST", "postgis")
                .dependsOn(postgisContainer)
                .withNetwork(testNetwork)
                .withNetworkAliases("scorpio");
        scorpioContainer.start();

        blockchainAdapterContainer = new GenericContainer<>(DockerImageName.parse("quay.io/digitelts/dlt-adapter:1.2.1"))
                .withExposedPorts(8080)
                .withEnv("PRIVATE_KEY", "0xe2afef2c880b138d741995ba56936e389b0b5dd2943e21e4363cc70d81c89346")
                .withNetwork(testNetwork)
                .withNetworkAliases("blockchain-adapter")
                .waitingFor(Wait.forHttp("/health").forStatusCode(200));
        blockchainAdapterContainer.start();
    }

    public static ContainerManager getInstance() {
        return INSTANCE;
    }

    @DynamicPropertySource
    public static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> String.format("r2dbc:pool:postgresql://%s:%s/desmos",
                postgresContainer.getHost(),
                postgresContainer.getFirstMappedPort()));
        registry.add("spring.r2dbc.username", postgresContainer::getUsername);
        registry.add("spring.r2dbc.password", postgresContainer::getPassword);
        registry.add("spring.flyway.url", postgresContainer::getJdbcUrl);
        registry.add("broker.externalDomain", ContainerManager::getBaseUriForScorpio);
        registry.add("evm-adapter.externalDomain", () -> String.format("http://%s:%s",
                blockchainAdapterContainer.getHost(),
                blockchainAdapterContainer.getFirstMappedPort()));
    }

    public static String getBaseUriForScorpio() {
        return "http://" + scorpioContainer.getHost() + ":" + scorpioContainer.getMappedPort(9090);
    }

    protected static String getBaseUriAlastriaAdapter() {
        return "http://" + blockchainAdapterContainer.getHost() + ":" + blockchainAdapterContainer.getMappedPort(8080);
    }

}
