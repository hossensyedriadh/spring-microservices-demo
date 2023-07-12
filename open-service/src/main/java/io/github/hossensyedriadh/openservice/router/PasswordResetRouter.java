package io.github.hossensyedriadh.openservice.router;

import io.github.hossensyedriadh.openservice.handler.PasswordResetHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class PasswordResetRouter {
    private final PasswordResetHandler passwordResetHandler;

    @Autowired
    public PasswordResetRouter(PasswordResetHandler passwordResetHandler) {
        this.passwordResetHandler = passwordResetHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> routePasswordReset() {
        return RouterFunctions.route(RequestPredicates.POST(PasswordResetRoutes.V1_FORGOT_PASSWORD.getRoute()),
                        this.passwordResetHandler::forgotPassword)
                .andRoute(RequestPredicates.POST(PasswordResetRoutes.V1_VERIFY_PASSWORD_RESET.getRoute())
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        this.passwordResetHandler::verify)
                .andRoute(RequestPredicates.POST(PasswordResetRoutes.V1_PASSWORD_RESET.getRoute())
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        this.passwordResetHandler::reset);
    }
}
