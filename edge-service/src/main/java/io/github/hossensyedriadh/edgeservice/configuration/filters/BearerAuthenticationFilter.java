package io.github.hossensyedriadh.edgeservice.configuration.filters;

import io.github.hossensyedriadh.edgeservice.configuration.authentication.bearer.service.BearerAuthenticationService;
import io.github.hossensyedriadh.edgeservice.configuration.authentication.service.GlobalUserDetailsService;
import io.github.hossensyedriadh.edgeservice.exception.GenericException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class BearerAuthenticationFilter implements WebFilter {
    private final BearerAuthenticationService bearerAuthenticationService;
    private final GlobalUserDetailsService globalUserDetailsService;
    private final ReactiveJwtDecoder reactiveJwtDecoder;

    @Value("${bearer-authentication.tokens.access-token.type}")
    private String accessTokenType;

    @Autowired
    public BearerAuthenticationFilter(BearerAuthenticationService bearerAuthenticationService,
                                      GlobalUserDetailsService globalUserDetailsService, ReactiveJwtDecoder reactiveJwtDecoder) {
        this.bearerAuthenticationService = bearerAuthenticationService;
        this.globalUserDetailsService = globalUserDetailsService;
        this.reactiveJwtDecoder = reactiveJwtDecoder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            String authorization = Objects.requireNonNull(request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);

            if (authorization.startsWith(this.accessTokenType + " ")) {
                String accessToken = authorization.substring(this.accessTokenType.concat(" ").length());

                return this.bearerAuthenticationService.isAccessTokenValid(accessToken).flatMap(valid -> {
                    if (valid) {
                        return this.reactiveJwtDecoder.decode(accessToken).flatMap(token -> this.globalUserDetailsService
                                .findByUsername(token.getClaimAsString("username")).flatMap(userDetails -> {
                                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
                                            null, userDetails.getAuthorities());
                                    authenticationToken.setDetails(exchange.getRequest());
                                    return ReactiveSecurityContextHolder.getContext().flatMap(s -> {
                                        s.setAuthentication(authenticationToken);
                                        return chain.filter(exchange);
                                    });
                                }));
                    } else {
                        return Mono.error(new GenericException(HttpStatus.UNAUTHORIZED, "Invalid / Expired access token", request.getPath().value()));
                    }
                });
            } else {
                return Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "Access token must be prepended with "
                        + "access token type, i.e.: 'Bearer '", request.getPath().value()));
            }
        } else {
            return Mono.error(new GenericException(HttpStatus.UNAUTHORIZED, "Missing access token", request.getPath().value()));
        }
    }
}
