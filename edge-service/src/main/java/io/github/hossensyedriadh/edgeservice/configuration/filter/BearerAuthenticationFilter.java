package io.github.hossensyedriadh.edgeservice.configuration.filter;

import io.github.hossensyedriadh.edgeservice.configuration.authentication.bearer.service.BearerAuthenticationService;
import io.github.hossensyedriadh.edgeservice.configuration.authentication.service.GlobalUserDetailsService;
import io.github.hossensyedriadh.edgeservice.exception.GenericException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class BearerAuthenticationFilter extends AbstractGatewayFilterFactory<BearerAuthenticationFilter.Config> {
    private final BearerAuthenticationService bearerAuthenticationService;
    private final GlobalUserDetailsService globalUserDetailsService;
    private final ReactiveJwtDecoder reactiveJwtDecoder;

    @Autowired
    public BearerAuthenticationFilter(BearerAuthenticationService bearerAuthenticationService,
                                      GlobalUserDetailsService globalUserDetailsService, ReactiveJwtDecoder reactiveJwtDecoder) {
        super(Config.class);
        this.bearerAuthenticationService = bearerAuthenticationService;
        this.globalUserDetailsService = globalUserDetailsService;
        this.reactiveJwtDecoder = reactiveJwtDecoder;
    }

    @Value("${bearer-authentication.tokens.access-token.type}")
    private String accessTokenType;

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest httpRequest = exchange.getRequest();

            if (httpRequest.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                String authorization = Objects.requireNonNull(httpRequest.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);

                if (authorization.startsWith(this.accessTokenType + " ")) {
                    String accessToken = authorization.substring(this.accessTokenType.concat(" ").length());

                    return this.bearerAuthenticationService.isAccessTokenValid(accessToken).flatMap(valid -> {
                        if (valid) {
                            return this.reactiveJwtDecoder.decode(accessToken).flatMap(token -> this.globalUserDetailsService
                                    .findByUsername(token.getSubject())
                                    .flatMap(userDetails -> {
                                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
                                                null, userDetails.getAuthorities());
                                        authenticationToken.setDetails(httpRequest);

                                        return ReactiveSecurityContextHolder.getContext().flatMap(context -> {
                                            context.setAuthentication(authenticationToken);
                                            return Mono.empty();
                                        }).then(chain.filter(exchange));
                                    }));
                        } else {
                            return Mono.error(new GenericException(HttpStatus.UNAUTHORIZED, "Invalid / Expired access token", httpRequest.getPath().value()));
                        }
                    });
                } else {
                    return Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "Access token must be prepended with "
                            + "access token type, i.e.: 'Bearer '", httpRequest.getPath().value()));
                }
            } else {
                return Mono.error(new GenericException(HttpStatus.UNAUTHORIZED, "Missing access token", httpRequest.getPath().value()));
            }
        });
    }

    public static class Config {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
