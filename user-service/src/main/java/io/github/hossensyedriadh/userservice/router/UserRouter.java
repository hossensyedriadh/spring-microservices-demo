package io.github.hossensyedriadh.userservice.router;

import io.github.hossensyedriadh.userservice.handler.UserHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class UserRouter {
    private final UserHandler userHandler;

    @Autowired
    public UserRouter(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions.route(RequestPredicates.GET(UserRouterConstants.V1_USERS_PAGEABLE.getRoute())
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        this.userHandler::users)
                .andRoute(RequestPredicates.GET(UserRouterConstants.V1_USERS_BY_USERNAME.getRoute())
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        this.userHandler::user)
                .andRoute(RequestPredicates.PUT(UserRouterConstants.V1_USER_UPDATE.getRoute())
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        this.userHandler::update)
                .andRoute(RequestPredicates.DELETE(UserRouterConstants.V1_USER_DELETE.getRoute())
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        this.userHandler::delete);
    }
}
