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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Service;
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

    private final String accessTokenSubject = "Access Token";

    private int refreshTokenValidity;

    private final String refreshTokenSubject = "Refresh Token";

    @Value("${bearer-authentication.tokens.access-token.validity-mins}")
    public void setAccessTokenValidity(int accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    @Value("${bearer-authentication.tokens.refresh-token.validity-mins}")
    public void setRefreshTokenValidity(int refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public Mono<String> generateAccessToken(Map<String, String> claims) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Instant.now().toEpochMilli());
        calendar.add(Calendar.MINUTE, this.accessTokenValidity);

        JWTCreator.Builder accessTokenBuilder = JWT.create().withSubject(this.accessTokenSubject)
                .withIssuer(this.tokenIssuer);
        claims.forEach(accessTokenBuilder::withClaim);

        return Mono.just(accessTokenBuilder.withNotBefore(new Date()).withIssuedAt(new Date())
                .withExpiresAt(calendar.getTime()).sign(Algorithm.RSA256(this.rsaPublicKey, this.rsaPrivateKey)));
    }

    public Mono<Boolean> isAccessTokenValid(String token, UserDetails userDetails) {
        Mono<Jwt> decodedJwt = NimbusReactiveJwtDecoder.withPublicKey(this.rsaPublicKey).build().decode(token)
                .onErrorResume(e -> Mono.error(new Exception(e.getMessage())));

        return decodedJwt.flatMap(jwt -> Mono.just(jwt.getClaimAsString("username").equals(userDetails.getUsername())
                && jwt.getSubject().equals(this.accessTokenSubject)));
    }

    public Mono<String> getRefreshToken(String username, Map<String, String> claims) {
        Mono<RefreshToken> refreshTokenMono = this.refreshTokenRepository.findByForUser(username);

        return refreshTokenMono.flatMap(refreshToken -> NimbusReactiveJwtDecoder.withPublicKey(this.rsaPublicKey).build().decode(refreshToken.getToken())
                .flatMap(token -> {
                    if (Objects.requireNonNull(token.getExpiresAt()).isAfter(Instant.now())
                            && token.getSubject().equals(this.refreshTokenSubject)) {
                        return Mono.just(refreshToken.getToken());
                    } else {
                        return this.createRefreshToken(username, claims);
                    }
                }).onErrorResume(e -> this.createRefreshToken(username, claims)))
                .onErrorResume(e -> this.createRefreshToken(username, claims));
    }

    private Mono<String> createRefreshToken(String username, Map<String, String> claims) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Instant.now().toEpochMilli());
        calendar.add(Calendar.MINUTE, this.refreshTokenValidity);

        JWTCreator.Builder refreshTokenBuilder = JWT.create().withSubject(this.refreshTokenSubject)
                .withIssuer(this.tokenIssuer);
        claims.forEach(refreshTokenBuilder::withClaim);

        String id = UUID.randomUUID().toString();

        String token = refreshTokenBuilder.withNotBefore(new Date()).withIssuedAt(new Date())
                .withExpiresAt(calendar.getTime()).withJWTId(id).sign(Algorithm.RSA256(this.rsaPublicKey, this.rsaPrivateKey));

        return this.persistRefreshToken(username, id, token);
    }

    private Mono<String> persistRefreshToken(String username, String id, String token) {
        RefreshToken refreshToken = new RefreshToken(id, token, username);
        return this.refreshTokenRepository.save(refreshToken).flatMap(s -> Mono.just(s.getToken()));
    }

    public Mono<Boolean> isRefreshTokenValid(String refreshToken) {
        Mono<Jwt> decodedJwt = NimbusReactiveJwtDecoder.withPublicKey(this.rsaPublicKey).build()
                .decode(refreshToken).onErrorResume(e -> Mono.error(new Exception(e.getMessage())));

        Mono<RefreshToken> refreshTokenMono = decodedJwt.flatMap(s -> this.refreshTokenRepository
                .findByForUser(s.getClaimAsString("username")));

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
