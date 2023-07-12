package io.github.hossensyedriadh.openservice.handler;

import io.github.hossensyedriadh.openservice.model.PasswordResetRequest;
import io.github.hossensyedriadh.openservice.service.passwordreset.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class PasswordResetHandler {
    private final PasswordResetService passwordResetService;

    @Autowired
    public PasswordResetHandler(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    public Mono<ServerResponse> forgotPassword(ServerRequest serverRequest) {
        String username = serverRequest.pathVariable("username");
        return this.passwordResetService.forgotPassword(username).then(ServerResponse.status(HttpStatus.ACCEPTED).build());
    }

    public Mono<ServerResponse> verify(ServerRequest serverRequest) {
        Mono<PasswordResetRequest> passwordResetRequestMono = serverRequest.bodyToMono(PasswordResetRequest.class);

        return passwordResetRequestMono.flatMap(passwordResetRequest -> this.passwordResetService.verifyPasswordReset(passwordResetRequest)
                .then(ServerResponse.status(HttpStatus.ACCEPTED).build()));
    }

    public Mono<ServerResponse> reset(ServerRequest serverRequest) {
        Mono<PasswordResetRequest> passwordResetRequestMono = serverRequest.bodyToMono(PasswordResetRequest.class);

        return passwordResetRequestMono.flatMap(passwordResetRequest -> this.passwordResetService.resetPassword(passwordResetRequest)
                .then(ServerResponse.noContent().build()));
    }
}
