package io.github.hossensyedriadh.openservice.router;

import io.github.hossensyedriadh.openservice.handler.SignupHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SignupRouter {
    private final SignupHandler signupHandler;

    @Autowired
    public SignupRouter(SignupHandler signupHandler) {
        this.signupHandler = signupHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> routeSignup() {
        return RouterFunctions.route(RequestPredicates.POST(SignupRoutes.V1_SIGNUP.getRoute())
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        this.signupHandler::signup)
                .andRoute(RequestPredicates.GET(SignupRoutes.V1_CHECK_USERNAME.getRoute()),
                        this.signupHandler::checkUsername);
    }
}
