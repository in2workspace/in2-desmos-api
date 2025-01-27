package es.in2.desmos.infrastructure.security;

import es.in2.desmos.infrastructure.configs.TrustFrameworkConfig;
import es.in2.desmos.infrastructure.security.filters.BearerTokenReactiveAuthenticationManager;
import es.in2.desmos.infrastructure.security.filters.ServerHttpBearerAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtVerifier;
    private final TrustFrameworkConfig trustFrameworkConfig;
    private final CorsConfig corsConfig;


    /**
     * For Spring Security webflux, a chain of filters will provide user authentication
     * and authorization; we add custom filters to enable JWT token approach.
     *
     * @param http An initial object to build common filter scenarios.
     *             Customized filters are added here.
     * @return SecurityWebFilterChain A filter chain for web exchanges that will
     * provide security
     **/
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/health").permitAll()
                        .pathMatchers("/api/v1/notifications/broker", "/api/v1/notifications/dlt").permitAll()
                        .pathMatchers("/api/v1/entities/*").authenticated() //replication endpoint
                        .pathMatchers("/api/v1/sync/p2p/*").authenticated() //synchronization endpoint
                        .anyExchange().authenticated()
                )
                .csrf(csrf -> csrf
                        .requireCsrfProtectionMatcher(ServerWebExchangeMatchers.pathMatchers("/api/v1/**"))
                        .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
                        .disable() // Disable CSRF protection for specific paths
                )
                .addFilterAt(bearerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()));
        return http.build();


    }

    /**
     * Use the already implemented logic by AuthenticationWebFilter and set a custom
     * converter that will handle requests containing a Bearer token inside
     * the HTTP Authorization header.
     * Set a dummy authentication manager to this filter, it's not needed because
     * the converter handles this.
     *
     * @return bearerAuthenticationFilter that will authorize requests containing a JWT
     */
    private AuthenticationWebFilter bearerAuthenticationFilter() {
        AuthenticationWebFilter bearerAuthenticationFilter;
        Function<ServerWebExchange, Mono<Authentication>> bearerConverter;
        ReactiveAuthenticationManager authManager;
        authManager = new BearerTokenReactiveAuthenticationManager();
        bearerAuthenticationFilter = new AuthenticationWebFilter(authManager);
        bearerConverter = new ServerHttpBearerAuthenticationConverter(jwtVerifier);
        bearerAuthenticationFilter
                .setAuthenticationConverter(bearerConverter);
        bearerAuthenticationFilter
                .setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/v1/**"));
        return bearerAuthenticationFilter;
    }
}
