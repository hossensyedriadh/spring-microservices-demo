package io.github.hossensyedriadh.openservice.service.signup;

import io.github.hossensyedriadh.openservice.entity.UserAccount;
import io.github.hossensyedriadh.openservice.enumerator.Authority;
import io.github.hossensyedriadh.openservice.exception.ResourceException;
import io.github.hossensyedriadh.openservice.repository.r2dbc.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public final class SignupServiceImpl implements SignupService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SignupServiceImpl(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<Void> signup(UserAccount userAccount) {
        userAccount.setPassword(this.passwordEncoder.encode(userAccount.getPassword()));
        userAccount.setAuthority(Authority.ROLE_CUSTOMER);

        return this.userAccountRepository.findById(userAccount.getUsername())
                .flatMap(user -> {
                    if (user != null) {
                        return Mono.error(new ResourceException("Duplicate user", HttpStatus.BAD_REQUEST));
                    }
                    return Mono.empty();
                }).switchIfEmpty(this.userAccountRepository.save(userAccount).doOnSuccess(this::sendSignupEmail)).then();
    }

    @Override
    public Mono<Boolean> checkUsername(String username) {
        return this.userAccountRepository.findById(username).hasElement().map(exists -> !exists);
    }

    private void sendSignupEmail(UserAccount account) {
        //todo: to be implemented using kafka
    }
}
