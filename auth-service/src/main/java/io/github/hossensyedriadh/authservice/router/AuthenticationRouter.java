package io.github.hossensyedriadh.authservice.router;

import io.github.hossensyedriadh.authservice.handler.AuthenticationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AuthenticationRouter {
    private final AuthenticationHandler authenticationHandler;

    @Autowired
    public AuthenticationRouter(AuthenticationHandler authenticationHandler) {
        this.authenticationHandler = authenticationHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions.route(RequestPredicates.POST(AuthenticationRouterConstants.V1_AUTHENTICATE.getRoute())
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        this.authenticationHandler::authenticate)
                .andRoute(RequestPredicates.POST(AuthenticationRouterConstants.V1_RENEW_ACCESS_TOKEN.getRoute())
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        this.authenticationHandler::renewAccessToken);
    }
}
