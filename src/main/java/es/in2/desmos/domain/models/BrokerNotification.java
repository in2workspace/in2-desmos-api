package es.in2.desmos.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record BrokerNotification(
        @JsonProperty("id")
        @NotBlank(message = "id cannot be blank")
        String id,

        @JsonProperty("type")
        @NotBlank(message = "type cannot be blank")
        String type,

        @JsonProperty("data")
        @NotNull(message = "data cannot be null")
        List<@NotNull Map<@NotBlank String, Object>> data,

        @JsonProperty("subscriptionId")
        @NotBlank(message = "subscriptionId cannot be blank")
        String subscriptionId,

        @JsonProperty("notifiedAt")
        @NotBlank(message = "notifiedAt cannot be blank")
        String notifiedAt
) {
}