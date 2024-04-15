package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Builder
public record BlockchainTxPayload(
        @JsonProperty("eventType")
        @NotBlank(message = "eventType must not be blank")
        String eventType,

        @JsonProperty("iss")
        @NotBlank(message = "iss must not be blank")
        String organizationId,

        @JsonProperty("entityId")
        @NotBlank(message = "entityId must not be blank")
        String entityId,

        @JsonProperty("previousEntityHash")
        @NotBlank(message = "previousEntityHash must not be blank")
        String previousEntityHash,

        @JsonProperty("dataLocation")
        @NotBlank(message = "dataLocation must not be blank")
        @URL(message = "dataLocation must be a valid URL")
        String dataLocation,

        @JsonProperty("relevantMetadata")
        @NotNull(message = "relevantMetadata must not be null")
        List<@NotBlank String> metadata
) {
}