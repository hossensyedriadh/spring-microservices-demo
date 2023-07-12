package io.github.hossensyedriadh.openservice.service.passwordreset;

import io.github.hossensyedriadh.openservice.model.PasswordResetRequest;
import reactor.core.publisher.Mono;

public sealed interface PasswordResetService permits PasswordResetServiceImpl {
    Mono<Void> forgotPassword(String username);

    Mono<Void> verifyPasswordReset(PasswordResetRequest passwordResetRequest);

    Mono<Void> resetPassword(PasswordResetRequest passwordResetRequest);
}
