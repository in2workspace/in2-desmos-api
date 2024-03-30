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
    private static final GenericContainer<?> scorpioContainer;
    private static final GenericContainer<?> postgisContainer;
    private static final GenericContainer<?> blockchainAdapterContainer;
    private static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("it_dbd")
                .withUsername("postgres")
                .withPassword("postgres")
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

        blockchainAdapterContainer = new GenericContainer<>(DockerImageName.parse("quay.io/digitelts/dlt-adapter:1.3"))
                .withExposedPorts(8080)
                .withEnv("PRIVATE_KEY", "0x304d170fb355df65cc17ef7934404fe9baee73a1244380076436dec6fafb1e1f")
                .withEnv("DOME_EVENTS_CONTRACT_ADDRESS", "")
                .withEnv("RPC_ADDRESS", "http://blockchain-testnode.infra.svc.cluster.local:8545/")
                .withEnv("DOME_PRODUCTION_BLOCK_NUMBER", "0")
                .withEnv("ISS", "0x9eb763b0a6b7e617d56b85f1df943f176018c8eedb2dd9dd37c0bd77496833fe")
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
        registry.add("spring.r2dbc.url", () -> String.format("r2dbc:pool:postgresql://%s:%s/it_db",
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

    public static String getBaseUriBlockchainAdapter() {
        return "http://" + blockchainAdapterContainer.getHost() + ":" + blockchainAdapterContainer.getMappedPort(8080);
    }

}
