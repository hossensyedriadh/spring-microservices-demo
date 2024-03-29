package io.github.hossensyedriadh.authservice.configuration.authentication.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import io.github.hossensyedriadh.authservice.entity.RefreshToken;
import io.github.hossensyedriadh.authservice.repository.mongo.RefreshTokenRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.*;

@Log4j
@Service
public class BearerAuthenticationService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final RSAPublicKey rsaPublicKey;
    private final RSAPrivateKey rsaPrivateKey;

    @Autowired
    public BearerAuthenticationService(RefreshTokenRepository refreshTokenRepository,
                                       RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.rsaPublicKey = rsaPublicKey;
        this.rsaPrivateKey = rsaPrivateKey;
    }

    @Value("${spring.application.name}")
    private String tokenIssuer;

    private int accessTokenValidity;

    private static final String accessTokenType = "Access Token";

    private int refreshTokenValidity;

    private static final String refreshTokenType = "Refresh Token";

    @Value("${bearer-authentication.tokens.access-token.validity-mins}")
    public void setAccessTokenValidity(int accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    @Value("${bearer-authentication.tokens.refresh-token.validity-mins}")
    public void setRefreshTokenValidity(int refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public Mono<String> generateAccessToken(String principal, Map<String, String> claims) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Instant.now().toEpochMilli());
        calendar.add(Calendar.MINUTE, this.accessTokenValidity);

        JWTCreator.Builder accessTokenBuilder = JWT.create().withSubject(principal)
                .withIssuer(this.tokenIssuer);
        claims.put("type", accessTokenType);
        claims.forEach(accessTokenBuilder::withClaim);

        return Mono.just(accessTokenBuilder.withNotBefore(new Date()).withIssuedAt(new Date())
                .withExpiresAt(calendar.getTime()).withAudience("edge-service", "product-service", "order-service", "user-service")
                .sign(Algorithm.RSA256(this.rsaPublicKey, this.rsaPrivateKey)));
    }

    public Mono<String> getRefreshToken(String username, Map<String, String> claims) {
        Mono<RefreshToken> refreshTokenMono = this.refreshTokenRepository.findByForUser(username);

        return refreshTokenMono.hasElement().flatMap(v -> {
            if (v) {
                return refreshTokenMono.flatMap(refreshToken -> NimbusReactiveJwtDecoder.withPublicKey(this.rsaPublicKey).build().decode(refreshToken.getToken())
                        .flatMap(token -> {
                            if (Objects.requireNonNull(token.getExpiresAt()).isAfter(Instant.now())
                                    && token.getClaimAsString("type").equals(refreshTokenType) &&
                                    new HashSet<>(token.getAudience()).containsAll(List.of("edge-service", "auth-service"))) {
                                return Mono.just(refreshToken.getToken());
                            } else {
                                return this.createRefreshToken(username, claims);
                            }
                        }).onErrorResume(e -> this.createRefreshToken(username, claims)));
            } else {
                return this.createRefreshToken(username, claims);
            }
        });
    }

    private Mono<String> createRefreshToken(String username, Map<String, String> claims) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Instant.now().toEpochMilli());
        calendar.add(Calendar.MINUTE, this.refreshTokenValidity);

        JWTCreator.Builder refreshTokenBuilder = JWT.create().withSubject(username)
                .withIssuer(this.tokenIssuer);
        claims.put("type", refreshTokenType);
        claims.forEach(refreshTokenBuilder::withClaim);

        String id = UUID.randomUUID().toString();

        String token = refreshTokenBuilder.withNotBefore(new Date()).withIssuedAt(new Date())
                .withExpiresAt(calendar.getTime()).withJWTId(id).withAudience("edge-service", "auth-service")
                .sign(Algorithm.RSA256(this.rsaPublicKey, this.rsaPrivateKey));

        Flux<RefreshToken> refreshTokenFlux = this.refreshTokenRepository.findAllByForUser(username);

        return refreshTokenFlux.hasElements().flatMap(t -> {
            if (t) {
                return this.refreshTokenRepository.deleteAll(refreshTokenFlux).then(this.persistRefreshToken(username, id, token));
            } else {
                return this.persistRefreshToken(username, id, token);
            }
        });
    }

    private Mono<String> persistRefreshToken(String username, String id, String token) {
        RefreshToken refreshToken = new RefreshToken(id, token, username);
        return this.refreshTokenRepository.save(refreshToken).flatMap(s -> Mono.just(s.getToken()));
    }

    public Mono<Boolean> isRefreshTokenValid(String refreshToken) {
        Mono<Jwt> decodedJwt = NimbusReactiveJwtDecoder.withPublicKey(this.rsaPublicKey).build()
                .decode(refreshToken).onErrorResume(e -> Mono.error(new Exception(e.getMessage())));

        Mono<RefreshToken> refreshTokenMono = decodedJwt.flatMap(token -> {
            if (new HashSet<>(token.getAudience()).containsAll(List.of("edge-service", "auth-service")) && token.getClaimAsString("type").equals(refreshTokenType)
                    && Objects.requireNonNull(token.getExpiresAt()).isAfter(Instant.now()) && token.getNotBefore().isBefore(Instant.now())) {
                return this.refreshTokenRepository.findById(token.getId());
            } else {
                return Mono.empty();
            }
        });

        return refreshTokenMono.hasElement().flatMap(Mono::just)
                .switchIfEmpty(Mono.just(false));
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return NimbusReactiveJwtDecoder.withPublicKey(this.rsaPublicKey).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }
}
