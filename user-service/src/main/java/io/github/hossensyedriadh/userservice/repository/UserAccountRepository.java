package io.github.hossensyedriadh.userservice.repository;

import io.github.hossensyedriadh.userservice.entity.UserAccount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface UserAccountRepository extends R2dbcRepository<UserAccount, String>,
        ReactiveSortingRepository<UserAccount, String> {
    Flux<UserAccount> findAllBy(Pageable pageable);
}
