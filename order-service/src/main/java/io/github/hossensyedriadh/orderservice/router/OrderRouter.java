package io.github.hossensyedriadh.orderservice.router;

import io.github.hossensyedriadh.orderservice.handler.OrderHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class OrderRouter {
    private final OrderHandler orderHandler;

    @Autowired
    public OrderRouter(OrderHandler orderHandler) {
        this.orderHandler = orderHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions.route(RequestPredicates.GET(OrderRouterConstants.V1_ORDERS_PAGEABLE.getRoute()),
                        this.orderHandler::orders)
                .andRoute(RequestPredicates.GET(OrderRouterConstants.V1_ORDER_BY_ID.getRoute()),
                        this.orderHandler::order)
                .andRoute(RequestPredicates.POST(OrderRouterConstants.V1_CREATE_ORDER.getRoute())
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        this.orderHandler::create)
                .andRoute(RequestPredicates.PUT(OrderRouterConstants.V1_MODIFY_ORDER.getRoute())
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        this.orderHandler::modify);
    }
}
