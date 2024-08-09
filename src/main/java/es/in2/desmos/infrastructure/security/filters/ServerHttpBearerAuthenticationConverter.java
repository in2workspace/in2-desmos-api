package es.in2.desmos.infrastructure.security.filters;

import com.nimbusds.jwt.SignedJWT;
import es.in2.desmos.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This converter extracts a bearer token from a WebExchange and
 * returns an Authentication object if the JWT token is valid.
 * Validity means is well formed and signature is correct
 */
@RequiredArgsConstructor
public class ServerHttpBearerAuthenticationConverter implements Function<ServerWebExchange, Mono<Authentication>> {

    private static final String BEARER = "Bearer ";
    private static final Predicate<String> matchBearerLength = authValue -> authValue.length() > BEARER.length();
    private static final Function<String,Mono<String>> isolateBearerValue = authValue -> Mono.justOrEmpty(authValue.substring(BEARER.length()));

    private final JwtTokenProvider jwtVerifier;

    @Override
    public Mono<Authentication> apply(ServerWebExchange serverWebExchange) {
        return Mono.justOrEmpty(serverWebExchange)
                .flatMap(ServerHttpBearerAuthenticationConverter::extract)
                .filter(matchBearerLength)
                .flatMap(isolateBearerValue)
                .flatMap(jwtString ->
                        jwtVerifier.validateSignedJwt(jwtString, serverWebExchange.getRequest().getHeaders().getFirst("origin")))
                .flatMap(ServerHttpBearerAuthenticationConverter::create).log();
    }

    public static Mono<String> extract(ServerWebExchange serverWebExchange) {
        return Mono.justOrEmpty(serverWebExchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION));
    }

    public static Mono<Authentication> create(SignedJWT signedJWTMono) {
        String subject;
        //String auths;
        try {
            subject = signedJWTMono.getJWTClaimsSet().getSubject();
            //auths = (String) signedJWTMono.getJWTClaimsSet().getClaim("roles");
        } catch (ParseException | java.text.ParseException e) {
            return Mono.empty();
        }
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        return  Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(subject, null, authorities));
    }

}
