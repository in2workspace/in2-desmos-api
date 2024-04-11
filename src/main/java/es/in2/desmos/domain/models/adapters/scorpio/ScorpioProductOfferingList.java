package es.in2.desmos.domain.models.adapters.scorpio;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ScorpioProductOfferingList(
        @NotNull
        List<@NotNull ScorpioProductOffering> productOfferingsList) {
}
