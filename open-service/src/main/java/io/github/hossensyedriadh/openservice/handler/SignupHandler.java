package io.github.hossensyedriadh.openservice.handler;

import io.github.hossensyedriadh.openservice.entity.UserAccount;
import io.github.hossensyedriadh.openservice.service.signup.SignupService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class SignupHandler {
    private final SignupService signupService;

    @Autowired
    public SignupHandler(SignupService signupService) {
        this.signupService = signupService;
    }


    public Mono<ServerResponse> signup(ServerRequest serverRequest) {
        Mono<UserAccount> accountMono = serverRequest.bodyToMono(UserAccount.class);

        return accountMono.flatMap(account -> this.signupService.signup(account).then(ServerResponse.noContent().build()));
    }

    public Mono<ServerResponse> checkUsername(ServerRequest serverRequest) {
        String username = serverRequest.pathVariable("username");

        return this.signupService.checkUsername(username).flatMap(isUnique -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("isUnique", isUnique);
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonObject.toString());
        });
    }
}
