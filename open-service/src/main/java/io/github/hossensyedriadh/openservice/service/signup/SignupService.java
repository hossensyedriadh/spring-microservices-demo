package io.github.hossensyedriadh.openservice.service.signup;

import io.github.hossensyedriadh.openservice.entity.UserAccount;
import reactor.core.publisher.Mono;

public sealed interface SignupService permits SignupServiceImpl {
    Mono<Void> signup(UserAccount userAccount);

    Mono<Boolean> checkUsername(String username);
}
