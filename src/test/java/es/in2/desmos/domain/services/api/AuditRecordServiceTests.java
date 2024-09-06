package es.in2.desmos.domain.services.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.desmos.domain.models.*;
import es.in2.desmos.domain.repositories.AuditRecordRepository;
import es.in2.desmos.domain.services.api.impl.AuditRecordServiceImpl;
import es.in2.desmos.domain.services.broker.BrokerPublisherService;
import es.in2.desmos.objectmothers.AuditRecordMother;
import es.in2.desmos.objectmothers.EntityMother;
import es.in2.desmos.objectmothers.MVAuditServiceEntity4DataNegotiationMother;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditRecordServiceTests {

    @Mock
    private AuditRecordRepository auditRecordRepository;

    @Mock
    private BlockchainTxPayload blockchainTxPayload;

    @Mock
    private BrokerPublisherService brokerPublisherService;

    @SuppressWarnings("CanBeFinal")
    @Spy
    private static ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AuditRecordServiceImpl auditRecordService;

    @Captor
    private ArgumentCaptor<AuditRecord> auditRecordArgumentCaptor;

    @Test
    void testBuildAndSaveAuditRecordFromBrokerNotification() throws Exception {
        // Arrange
        String processId = "processId";
        String sampleDataLocation = "http://localhost:8080/ngsi-ld/v1/entities/" +
                "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69" +
                "?hl=03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", "entityId");
        dataMap.put("type", "entityType");
        AuditRecordStatus status = AuditRecordStatus.CREATED;
        AuditRecord lastAuditRecordRegistered = AuditRecord.builder()
                .hashLink("previousHashLink")
                .build();

        when(objectMapper.writeValueAsString(any())).thenReturn("sampleData");
        when(auditRecordRepository.findMostRecentAuditRecord()).thenReturn(Mono.just(lastAuditRecordRegistered));
        when(blockchainTxPayload.dataLocation()).thenReturn(sampleDataLocation);
        when(auditRecordRepository.save(any())).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = auditRecordService.buildAndSaveAuditRecordFromBrokerNotification(processId, dataMap, status, blockchainTxPayload);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(auditRecordRepository, times(1)).save(any());
    }

    @Test
    void testBuildAndSaveAuditRecordFromBrokerNotification_BlockchainTxPayloadNull() throws JsonProcessingException {
        // Arrange
        String processId = "processId";
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", "entityId");
        dataMap.put("type", "entityType");
        AuditRecordStatus status = AuditRecordStatus.CREATED;
        AuditRecord lastAuditRecordRegistered = AuditRecord.builder()
                .hashLink("previousHashLink")
                .build();

        when(objectMapper.writeValueAsString(any())).thenReturn("sampleData");
        when(auditRecordRepository.findMostRecentAuditRecord()).thenReturn(Mono.just(lastAuditRecordRegistered));
        when(auditRecordRepository.save(any())).thenReturn(Mono.empty());

        // Act
        auditRecordService.buildAndSaveAuditRecordFromBrokerNotification(processId, dataMap, status, null).block();

        // Assert
        verify(auditRecordRepository, times(1)).save(any());
    }

    @Test
    void testBuildAndSaveAuditRecordFromBrokerNotification_ExceptionHandling() throws Exception {
        // Arrange
        String processId = "processId";
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", "entityId");
        dataMap.put("type", "entityType");
        AuditRecordStatus status = AuditRecordStatus.CREATED;
        AuditRecord lastAuditRecordRegistered = AuditRecord.builder()
                .hashLink("previousHashLink")
                .build();

        when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
        when(auditRecordRepository.findMostRecentAuditRecord()).thenReturn(Mono.just(lastAuditRecordRegistered));

        // Act
        Mono<Void> result = auditRecordService.buildAndSaveAuditRecordFromBrokerNotification(processId, dataMap, status, blockchainTxPayload);

        // Assert
        StepVerifier.create(result)
                .expectError(JsonProcessingException.class)
                .verify();
    }


    @Test
    void itShouldBuildAndSaveAuditRecordFromDataSync() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {
        String processId = "0";
        String issuer = "http://example.org";
        MVAuditServiceEntity4DataNegotiation mvAuditServiceEntity4DataNegotiation = MVAuditServiceEntity4DataNegotiationMother.sample1();
        AuditRecordStatus status = AuditRecordStatus.RETRIEVED;
        AuditRecord expectedAuditRecord = AuditRecordMother.createAuditRecordFromMVAuditServiceEntity4DataNegotiation("http://example.org", mvAuditServiceEntity4DataNegotiation, status);

        when(auditRecordRepository.findMostRecentAuditRecord()).thenReturn(Mono.just(new AuditRecord()));
        when(auditRecordRepository.save(any())).thenReturn(Mono.just(expectedAuditRecord));

        var result = auditRecordService.buildAndSaveAuditRecordFromDataSync(processId, issuer, mvAuditServiceEntity4DataNegotiation, status);

        StepVerifier
                .create(result)
                .verifyComplete();

        verify(auditRecordRepository, times(1)).save(auditRecordArgumentCaptor.capture());
        verifyNoMoreInteractions(auditRecordRepository);

        assertThat(auditRecordArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "hash", "hashLink")
                .isEqualTo(expectedAuditRecord);
    }

    @Test
    void testBuildAndSaveAuditRecordFromBlockchainNotification() throws Exception {
        // Arrange
        String processId = "processId";
        String sampleDataLocation = "http://localhost:8080/ngsi-ld/v1/entities/" +
                "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69" +
                "?hl=03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
        BlockchainNotification blockchainNotification = BlockchainNotification.builder()
                .dataLocation(sampleDataLocation)
                .eventType("entityType")
                .build();
        String retrievedBrokerEntity = "sampleData";
        AuditRecordStatus status = AuditRecordStatus.CREATED;
        AuditRecord lastAuditRecordRegistered = AuditRecord.builder()
                .hashLink("previousHashLink")
                .build();

        when(objectMapper.writeValueAsString(any())).thenReturn("sampleData");
        when(auditRecordRepository.findMostRecentAuditRecord()).thenReturn(Mono.just(lastAuditRecordRegistered));
        when(auditRecordRepository.save(any())).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, retrievedBrokerEntity, status);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(auditRecordRepository, times(1)).save(any());
    }

    @Test
    void testBuildAndSaveAuditRecordFromBlockchainNotification_NullOrEmptyRetrievedEntity() throws JsonProcessingException {
        // Arrange
        String processId = "processId";
        String entityId = "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69";
        String entityHashLink = "dbff5341acad5e2a58db4efd5e72e2d9a0a843a28e02b1183c68162d0a3a3de6";
        String dataLocation = "http://localhost:8080/ngsi-ld/v1/entities/" + entityId + "?hl=" + entityHashLink;
        BlockchainNotification blockchainNotification = BlockchainNotification.builder()
                .dataLocation(dataLocation).build();
        AuditRecordStatus auditRecordStatus = AuditRecordStatus.RETRIEVED;
        AuditRecord lastAuditRecordRegistered = AuditRecord.builder()
                .hashLink("previousHashLink")
                .build();

        when(objectMapper.writeValueAsString(any())).thenReturn("sampleData");
        when(auditRecordRepository.findMostRecentAuditRecord()).thenReturn(Mono.just(lastAuditRecordRegistered));
        when(auditRecordRepository.save(any())).thenReturn(Mono.empty());

        // Act
        auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, null, auditRecordStatus).block();

        // Assert
        verify(auditRecordRepository, times(1)).save(argThat(auditRecord ->
                auditRecord.getEntityHash().isEmpty() && auditRecord.getEntityHashLink().isEmpty()));
    }

    @Test
    void testBuildAndSaveAuditRecordFromBlockchainNotification_ExceptionHandling() throws JsonProcessingException {
        // Arrange
        String processId = "processId";
        String entityId = "urn:ngsi-ld:ProductOffering:38088145-aef3-440e-ab93-a33bc9bfce69";
        String entityHashLink = "dbff5341acad5e2a58db4efd5e72e2d9a0a843a28e02b1183c68162d0a3a3de6";
        String dataLocation = "http://localhost:8080/ngsi-ld/v1/entities/" + entityId + "?hl=" + entityHashLink;
        BlockchainNotification blockchainNotification = BlockchainNotification.builder()
                .dataLocation(dataLocation).build();
        AuditRecordStatus status = AuditRecordStatus.RETRIEVED;
        AuditRecord lastAuditRecordRegistered = AuditRecord.builder()
                .hashLink("previousHashLink")
                .build();

        when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
        when(auditRecordRepository.findMostRecentAuditRecord()).thenReturn(Mono.just(lastAuditRecordRegistered));

        // Act
        Mono<Void> result = auditRecordService.buildAndSaveAuditRecordFromBlockchainNotification(processId, blockchainNotification, "retrievedBrokerEntity", status);

        // Assert
        StepVerifier.create(result)
                .expectError(JsonProcessingException.class)
                .verify();
    }

    @Test
    void testFetchLatestProducerEntityHashByEntityId() {
        //Arrange
        String processId = "processId";
        String entityId = "entityId";
        AuditRecord auditRecord = AuditRecord.builder()
                .entityHash("entityHash")
                .build();
        when(auditRecordService.getLastPublishedAuditRecordForProducerByEntityId(processId, entityId)).thenReturn(Mono.just(auditRecord));
        // Act
        String actualEntityHash = auditRecordService.fetchLatestProducerEntityHashByEntityId(processId, entityId).block();
        // Assert
        assertEquals(auditRecord.getEntityHash(), actualEntityHash);
    }

    @Test
    void testFindLatestConsumerPublishedAuditRecordByEntityId() {
        // Arrange
        String processId = "processId";
        String entityId = "entityId";
        AuditRecord auditRecord = new AuditRecord();
        auditRecord.setEntityHash("entityHash");
        when(auditRecordRepository.findLastPublishedConsumerAuditRecordByEntityId(entityId)).thenReturn(Mono.just(auditRecord));
        // Act
        AuditRecord actualAuditRecord = auditRecordService.findLatestConsumerPublishedAuditRecordByEntityId(processId, entityId).block();
        // Assert
        assertEquals(auditRecord, actualAuditRecord);
    }

    @Test
    void testFindLatestConsumerPublishedAuditRecord() {
        // Arrange
        String processId = "processId";
        AuditRecord auditRecord = new AuditRecord();
        auditRecord.setEntityHash("entityHash");
        when(auditRecordRepository.findLastPublishedConsumerAuditRecord()).thenReturn(Mono.just(auditRecord));
        // Act
        AuditRecord actualAuditRecord = auditRecordService.findLatestConsumerPublishedAuditRecord(processId).block();
        // Assert
        assertEquals(auditRecord, actualAuditRecord);
    }

    @Test
    void testFindLatestAuditRecordForEntity() {
        // Arrange
        String processId = "processId";
        String entityId = "entityId";
        AuditRecord auditRecord = new AuditRecord();
        auditRecord.setEntityHash("entityHash");
        when(auditRecordRepository.findMostRecentPublishedOrDeletedByEntityId(entityId)).thenReturn(Mono.just(auditRecord));
        // Act
        AuditRecord actualAuditRecord = auditRecordService.findLatestAuditRecordForEntity(processId, entityId).block();
        // Assert
        assertEquals(auditRecord, actualAuditRecord);
    }

    @Test
    void testFetchMostRecentAuditRecord_EmptyTable() {
        // Arrange
        when(auditRecordRepository.findMostRecentAuditRecord()).thenReturn(Mono.empty());
        when(auditRecordRepository.count()).thenReturn(Mono.just(0L));

        // Act
        auditRecordService.fetchMostRecentAuditRecord().block();

        // Assert
        verify(auditRecordRepository, times(1)).findMostRecentAuditRecord();
    }

    @Test
    void testFetchMostRecentAuditRecord_NonEmptyTable_NoRecentRecord() {
        // Arrange
        when(auditRecordRepository.findMostRecentAuditRecord()).thenReturn(Mono.empty());
        when(auditRecordRepository.count()).thenReturn(Mono.just(1L));
        // Act
        Mono<AuditRecord> mono = auditRecordService.fetchMostRecentAuditRecord();
        // Assert
        assertThrows(NoSuchElementException.class, mono::block);
        // Verify
        verify(auditRecordRepository, times(1)).findMostRecentAuditRecord();
    }


    @Test
    void itShouldReturnErrorWhenAuditRecordCreatesIncorrectJson() throws JsonProcessingException, JSONException, NoSuchAlgorithmException {
        String processId = "0";
        String issuer = "http://example.org";
        MVAuditServiceEntity4DataNegotiation mvAuditServiceEntity4DataNegotiation = MVAuditServiceEntity4DataNegotiationMother.sample1();
        AuditRecordStatus status = AuditRecordStatus.RETRIEVED;

        when(auditRecordRepository.findMostRecentAuditRecord()).thenReturn(Mono.just(new AuditRecord()));

        when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        var result = auditRecordService.buildAndSaveAuditRecordFromDataSync(processId, issuer, mvAuditServiceEntity4DataNegotiation, status);

        StepVerifier
                .create(result)
                .expectErrorMatches(throwable -> throwable instanceof JsonProcessingException)
                .verify();
    }

    @Test
    void testSetAuditRecordLock() {
        String processId = "process1";
        String id = "record1";
        boolean isLocked = true;

        StepVerifier.create(auditRecordService.setAuditRecordLock(processId, id, isLocked))
                .expectComplete()
                .verify();

        StepVerifier.create(auditRecordService.isAuditRecordUnlocked(processId, id))
                .consumeNextWith(result -> assertThat(result).isFalse())
                .expectComplete()
                .verify();
    }

    @Test
    void testSetAuditRecordUnlock() {
        String processId = "process1";
        String id = "record1";

        StepVerifier.create(auditRecordService.setAuditRecordLock(processId, id, true))
                .expectComplete()
                .verify();

        StepVerifier.create(auditRecordService.setAuditRecordLock(processId, id, false))
                .expectComplete()
                .verify();

        StepVerifier.create(auditRecordService.isAuditRecordUnlocked(processId, id))
                .consumeNextWith(result -> assertThat(result).isTrue())
                .expectComplete()
                .verify();
    }

    @Test
    void testUnlockAuditRecords() {
        String processId = "process2";
        String id1 = "record1";
        String id2 = "record2";

        StepVerifier.create(auditRecordService.setAuditRecordLock(processId, id1, true))
                .expectComplete()
                .verify();

        StepVerifier.create(auditRecordService.setAuditRecordLock(processId, id2, true))
                .expectComplete()
                .verify();

        auditRecordService.unlockAuditRecords(processId);

        StepVerifier.create(auditRecordService.isAuditRecordUnlocked(processId, id1))
                .consumeNextWith(result -> assertThat(result).isTrue())
                .expectComplete()
                .verify();

        StepVerifier.create(auditRecordService.isAuditRecordUnlocked(processId, id2))
                .consumeNextWith(result -> assertThat(result).isTrue())
                .expectComplete()
                .verify();
    }

    @Test
    void testUnlockAllAndTurnToLockAuditRecords() {
        String processId = "process2";
        String id1 = "record1";
        String id2 = "record2";

        StepVerifier.create(auditRecordService.setAuditRecordLock(processId, id1, true))
                .expectComplete()
                .verify();

        StepVerifier.create(auditRecordService.setAuditRecordLock(processId, id2, true))
                .expectComplete()
                .verify();

        auditRecordService.unlockAuditRecords(processId);

        StepVerifier.create(auditRecordService.isAuditRecordUnlocked(processId, id1))
                .consumeNextWith(result -> assertThat(result).isTrue())
                .expectComplete()
                .verify();

        StepVerifier.create(auditRecordService.isAuditRecordUnlocked(processId, id2))
                .consumeNextWith(result -> assertThat(result).isTrue())
                .expectComplete()
                .verify();

        String id3 = "record3";
        String id4 = "record4";

        StepVerifier.create(auditRecordService.setAuditRecordLock(processId, id3, true))
                .expectComplete()
                .verify();

        StepVerifier.create(auditRecordService.setAuditRecordLock(processId, id4, true))
                .expectComplete()
                .verify();

        StepVerifier.create(auditRecordService.isAuditRecordUnlocked(processId, id1))
                .consumeNextWith(result -> assertThat(result).isTrue())
                .expectComplete()
                .verify();

        StepVerifier.create(auditRecordService.isAuditRecordUnlocked(processId, id2))
                .consumeNextWith(result -> assertThat(result).isTrue())
                .expectComplete()
                .verify();

        StepVerifier.create(auditRecordService.isAuditRecordUnlocked(processId, id3))
                .consumeNextWith(result -> assertThat(result).isFalse())
                .expectComplete()
                .verify();

        StepVerifier.create(auditRecordService.isAuditRecordUnlocked(processId, id4))
                .consumeNextWith(result -> assertThat(result).isFalse())
                .expectComplete()
                .verify();
    }

    @Test
    void itShouldReturnExistingAuditRecordIfEntityHasAuditRecordAndHashIsEqualsToCurrentEntityHash() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {

        String processId = "0";
        var expectedAuditEntities = MVAuditServiceEntity4DataNegotiationMother.sample3and4();

        Mono<List<String>> entityIdsMono = Mono.just(List.of(expectedAuditEntities.get(0).id(), expectedAuditEntities.get(1).id()));

        when(brokerPublisherService.getEntityById(eq(processId), any()))
                .thenReturn(Mono.just(EntityMother.PRODUCT_OFFERING_3))
                .thenReturn(Mono.just(EntityMother.PRODUCT_OFFERING_4));

        when(auditRecordRepository.findLatestPublishedAuditRecordForProducerByEntityId(any()))
                .thenReturn(Mono.just(AuditRecordMother.list3And4().get(0)))
                .thenReturn(Mono.just(AuditRecordMother.list3And4().get(1)));

        Mono<List<MVAuditServiceEntity4DataNegotiation>> resultMono =
                auditRecordService.findCreateOrUpdateAuditRecordsByEntityIds(processId, expectedAuditEntities.get(0).type(), entityIdsMono);

        StepVerifier
                .create(resultMono)
                .assertNext(result ->
                        assertThat(result).isEqualTo(expectedAuditEntities))
                .verifyComplete();

        verify(auditRecordRepository, never()).save(any());
        verifyNoMoreInteractions(auditRecordRepository);
    }

    @Test
    void itShouldCreateAndReturnNewAuditRecordIfEntityHasAuditRecordAndHashIsNotEqualsToCurrentEntityHash() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {

        String processId = "0";
        var expectedAuditEntities = MVAuditServiceEntity4DataNegotiationMother.sample3and4NewHashlink();

        Mono<List<String>> entityIdsMono = Mono.just(List.of(expectedAuditEntities.get(0).id(), expectedAuditEntities.get(1).id()));

        when(brokerPublisherService.getEntityById(eq(processId), any()))
                .thenReturn(Mono.just(EntityMother.PRODUCT_OFFERING_3))
                .thenReturn(Mono.just(EntityMother.PRODUCT_OFFERING_4));

        when(auditRecordRepository.findLatestPublishedAuditRecordForProducerByEntityId(any()))
                .thenReturn(Mono.just(AuditRecordMother.list3OtherHashAnd4().get(0)))
                .thenReturn(Mono.just(AuditRecordMother.list3OtherHashAnd4().get(1)));

        when(auditRecordRepository.findMostRecentAuditRecord())
                .thenReturn(Mono.just(new AuditRecord()));

        when(auditRecordRepository.save(any()))
                .thenReturn(Mono.just(AuditRecordMother.list3OtherHashAnd4().get(0)));

        Mono<List<MVAuditServiceEntity4DataNegotiation>> resultMono =
                auditRecordService.findCreateOrUpdateAuditRecordsByEntityIds(processId, expectedAuditEntities.get(0).type(), entityIdsMono);

        StepVerifier
                .create(resultMono)
                .assertNext(result ->
                        assertThat(result).isEqualTo(expectedAuditEntities))
                .verifyComplete();

        verify(auditRecordRepository, times(1)).save(auditRecordArgumentCaptor.capture());
        verifyNoMoreInteractions(auditRecordRepository);

        var auditRecordSaved = auditRecordArgumentCaptor.getValue();
        assertThat(auditRecordSaved)
                .usingRecursiveComparison()
                .comparingOnlyFields("entityId", "entityType", "entityHash", "entityHashLink")
                .isEqualTo(
                        AuditRecord
                                .builder()
                                .entityId(expectedAuditEntities.get(0).id())
                                .entityType(expectedAuditEntities.get(0).type())
                                .entityHash(expectedAuditEntities.get(0).hash())
                                .entityHashLink(expectedAuditEntities.get(0).hashlink()));
    }

    @Test
    void itShouldCreateAndReturnAuditRecordIfEntityHasNotAuditRecord() throws JSONException, NoSuchAlgorithmException, JsonProcessingException {

        String processId = "0";
        var expectedAuditEntities = MVAuditServiceEntity4DataNegotiationMother.sample3EqualsHashAndHashlinkAnd4();

        Mono<List<String>> entityIdsMono = Mono.just(List.of(expectedAuditEntities.get(0).id(), expectedAuditEntities.get(1).id()));

        when(brokerPublisherService.getEntityById(eq(processId), any()))
                .thenReturn(Mono.just(EntityMother.PRODUCT_OFFERING_3))
                .thenReturn(Mono.just(EntityMother.PRODUCT_OFFERING_4));

        when(auditRecordRepository.findLatestPublishedAuditRecordForProducerByEntityId(any()))
                .thenReturn(Mono.empty())
                .thenReturn(Mono.just(AuditRecordMother.list3And4().get(1)));

        when(auditRecordRepository.findMostRecentAuditRecord())
                .thenReturn(Mono.just(new AuditRecord()));

        when(auditRecordRepository.save(any()))
                .thenReturn(Mono.just(AuditRecordMother.list3EqualsHashAndHashLinkAnd4().get(0)));

        Mono<List<MVAuditServiceEntity4DataNegotiation>> resultMono =
                auditRecordService.findCreateOrUpdateAuditRecordsByEntityIds(processId, expectedAuditEntities.get(0).type(), entityIdsMono);

        StepVerifier
                .create(resultMono)
                .assertNext(result ->
                        assertThat(result).isEqualTo(expectedAuditEntities))
                .verifyComplete();

        verify(auditRecordRepository, times(1)).save(auditRecordArgumentCaptor.capture());
        verifyNoMoreInteractions(auditRecordRepository);

        var auditRecordSaved = auditRecordArgumentCaptor.getValue();
        assertThat(auditRecordSaved)
                .usingRecursiveComparison()
                .comparingOnlyFields("entityId", "entityType", "entityHash", "entityHashLink")
                .isEqualTo(
                        AuditRecord
                                .builder()
                                .entityId(expectedAuditEntities.get(0).id())
                                .entityType(expectedAuditEntities.get(0).type())
                                .entityHash(expectedAuditEntities.get(0).hash())
                                .entityHashLink(expectedAuditEntities.get(0).hash()));
    }
}