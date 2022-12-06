package io.github.hossensyedriadh.authservice.repository.mongo;

import io.github.hossensyedriadh.authservice.entity.RefreshToken;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RefreshTokenRepository extends ReactiveMongoRepository<RefreshToken, String> {
    Mono<RefreshToken> findByForUser(String username);

    Flux<RefreshToken> findAllByForUser(String username);
}
