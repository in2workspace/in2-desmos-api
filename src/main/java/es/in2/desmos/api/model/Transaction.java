package es.in2.desmos.api.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table("transactions")
public class Transaction implements Persistable<UUID> {

    @Id
    @Column("id")
    private UUID id;

    @Column("transaction_id")
    private String transactionId;

    @Column("created_at")
    private Timestamp createdAt;

    @Column("entity_id")
    private String entityId;

    @Column("hashlink")
    private String hashlink;

    @Column("entity_type")
    private String entityType;

    @Column("entity_hash")
    private String hash;

    @Column("status")
    private TransactionStatus status;

    @Column("trader")
    private TransactionTrader trader;

    @Transient
    private boolean newTransaction;

    @Override
    @Transient
    public boolean isNew() {
        return this.newTransaction || id == null;
    }

}
