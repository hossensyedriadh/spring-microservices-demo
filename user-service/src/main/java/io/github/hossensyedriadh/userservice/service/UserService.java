package io.github.hossensyedriadh.userservice.service;

import io.github.hossensyedriadh.userservice.entity.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public sealed interface UserService permits UserServiceImpl {
    Mono<Page<UserAccount>> users(Pageable pageable);

    Mono<UserAccount> user(String username);

    Mono<UserAccount> update(UserAccount userAccount);

    Mono<Void> delete(String username);
}
