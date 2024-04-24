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
@Table("tx_payload_recover")
public class BlockchainTxPayloadRecover implements Persistable<UUID> {


    @Id
    @Column("id")
    private UUID id;

    @Column("process_id")
    private String processId;

    @Column("eventType")
    private String eventType;

    @Column("iss")
    private String organizationId;

    @Column("entityId")
    private String entityId;

    @Column("previousEntityHash")
    private String previousEntityHash;

    @Column("dataLocation")
    private String dataLocation;

    @Column("relevantMetadata")
    private String relevantMetadata;

    @Column("eventQueuePriority")
    private String eventQueuePriority;

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
