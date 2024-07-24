package es.in2.desmos.domain.repositories;

import es.in2.desmos.domain.models.DomeParticipant;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface DomeParticipantRepository extends ReactiveCrudRepository<DomeParticipant, UUID> {

    @Query("SELECT * FROM dome_participants WHERE ethereum_address=:ethereumAddress")
    Mono<DomeParticipant> findByEthereumAddress(String ethereumAddress);

}
