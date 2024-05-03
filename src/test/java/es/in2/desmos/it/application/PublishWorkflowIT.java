package es.in2.desmos.it.application;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PublishWorkflowIT {

    // 1. The NotificationController receives a BrokerNotification by POST request.
    // 3. The NotificationController processes the BrokerNotification, creates the
    // BlockchainTxPayload and publishes to the Blockchain.

}
