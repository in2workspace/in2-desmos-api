package es.in2.desmos.blockchain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record BlockchainNode(
        @JsonProperty("rpcAddress") String rpcAddress,
        @JsonProperty("userEthereumAddress") String userEthereumAddress,
        @JsonProperty("iss") String organizationId
) {
}
