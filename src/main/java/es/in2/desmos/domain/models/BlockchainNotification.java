package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record BlockchainNotification(
        @JsonProperty("id")
        @PositiveOrZero(message = "id must be positive or zero")
        long id,

        @JsonProperty("publisherAddress")
        @NotBlank(message = "publisherAddress must not be blank")
        String publisherAddress,

        @JsonProperty("eventType")
        @NotBlank(message = "eventType must not be blank")
        String eventType,

        @JsonProperty("timestamp")
        @PositiveOrZero(message = "timestamp must be positive or zero")
        long timestamp,

        @JsonProperty("dataLocation")
        @NotBlank(message = "dataLocation must not be blank")
        @URL(message = "dataLocation must be a valid URL")
        String dataLocation,

        @JsonProperty("relevantMetadata")
        @NotNull(message = "relevantMetadata must not be null")
        List<@NotBlank(message = "relevantMetadata must not be blank") String> relevantMetadata,

        @JsonProperty("entityIDHash")
        @NotBlank(message = "entityIDHash must not be blank")
        String entityId,

        @JsonProperty("previousEntityHash")
        @NotBlank(message = "previousEntityHash must not be blank")
        String previousEntityHash,

        @JsonProperty("ethereumAddress")
        @NotBlank(message = "ethereumAddress must not be blank")
        String ethereumAddress
) {
}