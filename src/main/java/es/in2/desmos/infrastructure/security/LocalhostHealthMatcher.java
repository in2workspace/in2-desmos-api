package es.in2.desmos.infrastructure.security;

import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class LocalhostHealthMatcher implements ServerWebExchangeMatcher {

    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        String host = Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
        if ("/health".equals(path) && ("127.0.0.1".equals(host) || "::1".equals(host))) {
            return MatchResult.match();
        }
        return MatchResult.notMatch();
    }
}
