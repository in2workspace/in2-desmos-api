package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Builder
public record BlockchainTxPayload(
        @JsonProperty("eventType") @NotBlank String eventType,
        @JsonProperty("iss") @NotBlank String organizationId,
        @JsonProperty("entityId") @NotBlank String entityId,
        @JsonProperty("previousEntityHash") @NotBlank String previousEntityHash,
        @JsonProperty("dataLocation") @NotBlank @URL String dataLocation,
        @JsonProperty("relevantMetadata") @NotNull List<@NotBlank String> metadata
) {
}