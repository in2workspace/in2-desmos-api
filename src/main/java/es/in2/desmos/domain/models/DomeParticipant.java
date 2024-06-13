package es.in2.desmos.domain.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("dome_participants")
public class DomeParticipant implements Persistable<UUID> {

    @Id
    @Column("id")
    private UUID id;

    @Column("ethereum_address")
    private String ethereumAddress;

    @Transient
    private boolean newTransaction;

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return this.newTransaction || id == null;
    }

}
