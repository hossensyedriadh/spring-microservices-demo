package io.github.hossensyedriadh.authservice.service.authentication;

import io.github.hossensyedriadh.authservice.model.AccessTokenRequest;
import io.github.hossensyedriadh.authservice.model.BearerTokenRequest;
import io.github.hossensyedriadh.authservice.model.BearerTokenResponse;
import reactor.core.publisher.Mono;

public sealed interface AuthenticationService permits AuthenticationServiceImpl {
    Mono<BearerTokenResponse> accessToken(BearerTokenRequest bearerTokenRequest);

    Mono<BearerTokenResponse> renewAccessToken(AccessTokenRequest accessTokenRequest);
}
