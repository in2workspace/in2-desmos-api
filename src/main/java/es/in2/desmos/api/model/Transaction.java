package es.in2.desmos.api.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@Table("transactions")
public class Transaction {

    @Id
    @Column("id")
    private UUID id;

    @Column("transaction_id")
    private String transactionId;

    @Column("created_at")
    private Timestamp createdAt;

    @Column("data_location")
    private String dataLocation;

    @Column("entity_id")
    private String entityId;

    @Column("entity_type")
    private String entityType;

    @Column("entity_hash")
    private String entityHash;

    @Column("status")
    private TransactionStatus status;

    @Column("trader")
    private TransactionTrader trader;

    @Column("hash")
    private String hash;

}
