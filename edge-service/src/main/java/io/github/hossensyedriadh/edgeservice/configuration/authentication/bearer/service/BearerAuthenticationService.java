package io.github.hossensyedriadh.edgeservice.configuration.authentication.bearer.service;

import io.github.hossensyedriadh.edgeservice.exception.GenericException;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.interfaces.RSAPublicKey;

@Log4j
@Service
public class BearerAuthenticationService {
    private final RSAPublicKey rsaPublicKey;

    @Autowired
    public BearerAuthenticationService(RSAPublicKey rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
    }

    private final String accessTokenSubject = "Access Token";

    public Mono<Boolean> isAccessTokenValid(String token) {
        Mono<Jwt> decodedJwt = NimbusReactiveJwtDecoder.withPublicKey(this.rsaPublicKey).build()
                .decode(token).onErrorResume(e -> Mono.error(new GenericException(HttpStatus.UNAUTHORIZED,
                        e.getMessage())));

        return decodedJwt.flatMap(jwt -> Mono.just(jwt.hasClaim("username") && jwt.hasClaim("authority")
                && jwt.getSubject().equals(this.accessTokenSubject)));
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return NimbusReactiveJwtDecoder.withPublicKey(this.rsaPublicKey).build();
    }
}
