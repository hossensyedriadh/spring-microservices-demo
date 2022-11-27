package io.github.hossensyedriadh.authservice.service.authentication;

import io.github.hossensyedriadh.authservice.configuration.authentication.service.BearerAuthenticationService;
import io.github.hossensyedriadh.authservice.configuration.authentication.service.BearerAuthenticationUserDetailsService;
import io.github.hossensyedriadh.authservice.exception.GenericException;
import io.github.hossensyedriadh.authservice.model.AccessTokenRequest;
import io.github.hossensyedriadh.authservice.model.BearerTokenRequest;
import io.github.hossensyedriadh.authservice.model.BearerTokenResponse;
import io.github.hossensyedriadh.authservice.repository.r2dbc.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public final class AuthenticationServiceImpl implements AuthenticationService {
    private final BearerAuthenticationService bearerAuthenticationService;
    private final BearerAuthenticationUserDetailsService bearerAuthenticationUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Value("${bearer-authentication.tokens.access-token.type}")
    private String accessTokenType;

    @Autowired
    public AuthenticationServiceImpl(BearerAuthenticationService bearerAuthenticationService,
                                     BearerAuthenticationUserDetailsService bearerAuthenticationUserDetailsService,
                                     PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.bearerAuthenticationService = bearerAuthenticationService;
        this.bearerAuthenticationUserDetailsService = bearerAuthenticationUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<BearerTokenResponse> accessToken(BearerTokenRequest bearerTokenRequest) {
        return this.bearerAuthenticationUserDetailsService.findByUsername(bearerTokenRequest.getId()).onErrorResume(e ->
                Mono.error(new GenericException(e.getMessage(), HttpStatus.BAD_REQUEST))).flatMap(userDetails -> {
            if (this.passwordEncoder.matches(bearerTokenRequest.getPassword(), userDetails.getPassword())) {
                String username = userDetails.getUsername();
                return this.userRepository.findById(userDetails.getUsername()).flatMap(user -> {
                    Map<String, String> claims = new HashMap<>();
                    claims.put("username", username);
                    claims.put("authority", String.valueOf(userDetails.getAuthorities().toArray()[0]));

                    Mono<String> accessTokenMono = this.bearerAuthenticationService.generateAccessToken(claims);
                    Mono<String> refreshTokenMono = this.bearerAuthenticationService.getRefreshToken(user.getUsername(), claims);

                    return accessTokenMono.flatMap(accessToken -> refreshTokenMono.flatMap(refreshToken ->
                            Mono.just(new BearerTokenResponse(accessToken, this.accessTokenType, refreshToken))));
                }).onErrorResume(e -> Mono.error(new GenericException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)));
            } else {
                return Mono.error(new GenericException("Invalid credentials", HttpStatus.UNAUTHORIZED));
            }
        });
    }

    @Override
    public Mono<BearerTokenResponse> renewAccessToken(AccessTokenRequest accessTokenRequest) {
        String refreshToken = accessTokenRequest.getRefresh_token();

        return this.bearerAuthenticationService.reactiveJwtDecoder().decode(refreshToken)
                .onErrorResume(e -> Mono.error(new GenericException("Invalid refresh token", HttpStatus.UNAUTHORIZED)))
                .flatMap(jwt -> this.bearerAuthenticationService.isRefreshTokenValid(refreshToken).flatMap(refreshTokenValid -> {
                    if (refreshTokenValid) {
                        return generateAccessToken(refreshToken, jwt);
                    } else {
                        return Mono.error(new GenericException("Invalid refresh token", HttpStatus.UNAUTHORIZED));
                    }
                }).onErrorResume(e -> Mono.error(new GenericException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR))));
    }

    private Mono<? extends BearerTokenResponse> generateAccessToken(String refreshToken, Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        Map<String, String> convertedClaims = new HashMap<>();

        convertedClaims.put("username", claims.get("username").toString());
        convertedClaims.put("authority", claims.get("authority").toString());

        Mono<String> accessTokenMono = this.bearerAuthenticationService
                .generateAccessToken(convertedClaims);

        return accessTokenMono.flatMap(renewedAccessToken -> Mono.just(new BearerTokenResponse(renewedAccessToken, this.accessTokenType, refreshToken)))
                .onErrorResume(e -> Mono.error(new GenericException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)));
    }
}
