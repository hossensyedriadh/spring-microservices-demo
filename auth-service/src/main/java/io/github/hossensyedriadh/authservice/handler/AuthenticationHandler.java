package io.github.hossensyedriadh.authservice.handler;

import io.github.hossensyedriadh.authservice.model.AccessTokenRequest;
import io.github.hossensyedriadh.authservice.model.BearerTokenRequest;
import io.github.hossensyedriadh.authservice.model.BearerTokenResponse;
import io.github.hossensyedriadh.authservice.service.authentication.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationHandler {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationHandler(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public Mono<ServerResponse> authenticate(ServerRequest serverRequest) {
        Mono<BearerTokenRequest> bearerTokenRequestMono = serverRequest.bodyToMono(BearerTokenRequest.class);

        return bearerTokenRequestMono.flatMap(r -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON).body(this.authenticationService.accessToken(r),
                        BearerTokenResponse.class));
    }

    public Mono<ServerResponse> renewAccessToken(ServerRequest serverRequest) {
        Mono<AccessTokenRequest> accessTokenRequestMono = serverRequest.bodyToMono(AccessTokenRequest.class);

        return accessTokenRequestMono.flatMap(r -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON).body(this.authenticationService.renewAccessToken(r),
                        BearerTokenResponse.class));
    }
}
