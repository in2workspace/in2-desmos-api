//package es.in2.desmos.api.model;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.Assert;
//import org.junit.jupiter.api.Test;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class BlockchainNotificationTest {
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Test
//    void testBuilderAndAccessors() {
//        List<String> metadata = Arrays.asList("meta1", "meta2");
//        BlockchainNotification.Id id = new BlockchainNotification.Id("Type", "Hex123");
//        BlockchainNotification.Timestamp timestamp = new BlockchainNotification.Timestamp("Type", "Hex456");
//
//        BlockchainNotification notification = BlockchainNotification.builder()
//                .id(id)
//                .publisherAddress("Address789")
//                .eventType("Event012")
//                .timestamp(timestamp)
//                .dataLocation("Location345")
//                .relevantMetadata(metadata)
//                .build();
//
//        assertEquals(id, notification.id());
//        assertEquals("Address789", notification.publisherAddress());
//        assertEquals("Event012", notification.eventType());
//        assertEquals(timestamp, notification.timestamp());
//        assertEquals("Location345", notification.dataLocation());
//        assertEquals(metadata, notification.relevantMetadata());
//    }
//
//    @Test
//    void testImmutability() {
//        List<String> metadata = Arrays.asList("meta1", "meta2");
//        BlockchainNotification notification = BlockchainNotification.builder()
//                .relevantMetadata(metadata)
//                .build();
//
//        List<String> eventMetadata = notification.relevantMetadata();
//        Assert.assertThrows(UnsupportedOperationException.class, () -> eventMetadata.add("meta3"));
//    }
//
//    @Test
//    void testSerialization() throws Exception {
//        BlockchainNotification.Id id = new BlockchainNotification.Id("Type", "Hex123");
//        BlockchainNotification notification = BlockchainNotification.builder()
//                .id(id)
//                .publisherAddress("Address789")
//                .build();
//
//        String json = objectMapper.writeValueAsString(notification);
//        assertTrue(json.contains("\"id\":{\"type\":\"Type\",\"hex\":\"Hex123\"}"));
//        assertTrue(json.contains("\"publisherAddress\":\"Address789\""));
//    }
//
//    @Test
//    void testDeserialization() throws Exception {
//        String json = "{\"id\":{\"type\":\"Type\",\"hex\":\"Hex123\"},\"publisherAddress\":\"Address789\"}";
//        BlockchainNotification notification = objectMapper.readValue(json, BlockchainNotification.class);
//
//        assertEquals("Type", notification.id().type());
//        assertEquals("Hex123", notification.id().hex());
//        assertEquals("Address789", notification.publisherAddress());
//    }
//
//}
