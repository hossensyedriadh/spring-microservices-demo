package io.github.hossensyedriadh.userservice.service;

import io.github.hossensyedriadh.userservice.entity.UserAccount;
import io.github.hossensyedriadh.userservice.exception.ResourceException;
import io.github.hossensyedriadh.userservice.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public final class UserServiceImpl implements UserService {
    private final UserAccountRepository userAccountRepository;

    @Autowired
    public UserServiceImpl(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public Mono<Page<UserAccount>> users(Pageable pageable) {
        return this.userAccountRepository.findAllBy(pageable).switchIfEmpty(Mono.error(new ResourceException(HttpStatus.NO_CONTENT)))
                .collectList().zipWith(this.userAccountRepository.count())
                .map(u -> new PageImpl<>(u.getT1(), pageable, u.getT2()));
    }

    @Override
    public Mono<UserAccount> user(String username) {
        return this.userAccountRepository.findById(username);
    }

    @Override
    public Mono<UserAccount> update(UserAccount userAccount) {
        Mono<UserAccount> matchedAccount = this.userAccountRepository.findById(userAccount.getUsername());

        return matchedAccount.map(u -> u).flatMap(s -> {
            if (userAccount.getAddress() != null) {
                s.setAddress(userAccount.getAddress());
            }

            if (userAccount.getEmail() != null) {
                s.setEmail(userAccount.getEmail());
            }
            return this.userAccountRepository.save(s);
        }).switchIfEmpty(Mono.error(new ResourceException("User not found with username: " + userAccount.getUsername(), HttpStatus.BAD_REQUEST)));
    }

    @Override
    public Mono<Void> delete(String username) {
        Mono<UserAccount> userAccountMono = this.userAccountRepository.findById(username);

        return userAccountMono.flatMap(this.userAccountRepository::delete).switchIfEmpty(Mono.error(
                new ResourceException("User not found with username: " +username, HttpStatus.BAD_REQUEST)));
    }
}
