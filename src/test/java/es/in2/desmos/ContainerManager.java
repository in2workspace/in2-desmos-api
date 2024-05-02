package es.in2.desmos;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SuppressWarnings("resource")
@Testcontainers
public class ContainerManager {

    private static final ContainerManager INSTANCE = new ContainerManager();
    private static final Network testNetworkA = Network.newNetwork();
    private static final GenericContainer<?> scorpioContainerA;
    private static final GenericContainer<?> postgisContainerA;
    private static final GenericContainer<?> blockchainAdapterContainerA;
    private static final PostgreSQLContainer<?> postgresContainerA;

    static {
        // Node A
        postgresContainerA = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("it_db")
                .withUsername("postgres")
                .withPassword("postgres")
                .withNetwork(testNetworkA)
                .withNetworkAliases("postgres");
        postgresContainerA.start();

        postgisContainerA = new GenericContainer<>(DockerImageName.parse("postgis/postgis"))
                .withExposedPorts(5432)
                .withEnv("POSTGRES_USER", "ngb")
                .withEnv("POSTGRES_PASSWORD", "ngb")
                .withEnv("POSTGRES_DB", "ngb")
                .withNetwork(testNetworkA)
                .withNetworkAliases("postgis");
        postgisContainerA.start();

        scorpioContainerA = new GenericContainer<>(DockerImageName.parse("scorpiobroker/all-in-one-runner:java-latest"))
                .withExposedPorts(9090)
                .withEnv("DBHOST", "postgis")
                .dependsOn(postgisContainerA)
                .withNetwork(testNetworkA)
                .withNetworkAliases("scorpio");
        scorpioContainerA.start();

        blockchainAdapterContainerA = new GenericContainer<>(DockerImageName.parse("quay.io/digitelts/dlt-adapter:1.3"))
                .withExposedPorts(8080)
                .withEnv("PRIVATE_KEY", "0x304d170fb355df65cc17ef7934404fe9baee73a1244380076436dec6fafb1e1f")
                .withEnv("DOME_EVENTS_CONTRACT_ADDRESS", "")
                .withEnv("RPC_ADDRESS", "http://blockchain-testnode.infra.svc.cluster.local:8545/")
                .withEnv("DOME_PRODUCTION_BLOCK_NUMBER", "0")
                .withEnv("ISS", "0x9eb763b0a6b7e617d56b85f1df943f176018c8eedb2dd9dd37c0bd77496833fe")
                .withNetwork(testNetworkA)
                .withNetworkAliases("dlt-adapter")
                .waitingFor(Wait.forHttp("/health").forStatusCode(200));
        blockchainAdapterContainerA.start();
    }

    public static ContainerManager getInstance() {
        return INSTANCE;
    }

    @DynamicPropertySource
    public static void postgresqlProperties(DynamicPropertyRegistry registry) {
        // Node A
        registry.add("spring.r2dbc.url", () -> String.format("r2dbc:pool:postgresql://%s:%s/it_db",
                postgresContainerA.getHost(),
                postgresContainerA.getFirstMappedPort()));
        registry.add("spring.r2dbc.username", postgresContainerA::getUsername);
        registry.add("spring.r2dbc.password", postgresContainerA::getPassword);
        registry.add("spring.flyway.url", postgresContainerA::getJdbcUrl);
        registry.add("broker.externalDomain", ContainerManager::getBaseUriForScorpioA);
        registry.add("dlt-adapter.externalDomain", ContainerManager::getBaseUriBlockchainAdapterA);
//        registry.add("dlt-adapter.externalDomain", () -> String.format("http://%s:%s",
//                blockchainAdapterContainerA.getHost(),
//                blockchainAdapterContainerA.getFirstMappedPort()));
    }

    public static String getBaseUriForScorpioA() {
        return "http://" + scorpioContainerA.getHost() + ":" + scorpioContainerA.getMappedPort(9090);
    }

    public static String getBaseUriBlockchainAdapterA() {
        return "http://" + blockchainAdapterContainerA.getHost() + ":" + blockchainAdapterContainerA.getMappedPort(8080);
    }

}