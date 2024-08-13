package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record MVAuditServiceEntity4DataNegotiation(@JsonProperty("id") @NotBlank String id,
                                                   @JsonProperty("hash") @NotBlank String hash,
                                                   @JsonProperty("hashlink") @NotBlank String hashlink) {
}
