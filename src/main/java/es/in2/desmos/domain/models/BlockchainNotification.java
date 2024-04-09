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
        @JsonProperty("id") @PositiveOrZero long id,
        @JsonProperty("publisherAddress") @NotBlank String publisherAddress,
        @JsonProperty("eventType") @NotBlank String eventType,
        @JsonProperty("timestamp") @PositiveOrZero long timestamp,
        @JsonProperty("dataLocation") @NotBlank @URL String dataLocation,
        @JsonProperty("relevantMetadata") @NotNull List<@NotBlank String> relevantMetadata,
        @JsonProperty("entityIDHash") @NotBlank String entityId,
        @JsonProperty("previousEntityHash") @NotBlank String previousEntityHash
) {
}