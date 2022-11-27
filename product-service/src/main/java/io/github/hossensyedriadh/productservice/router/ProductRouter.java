package io.github.hossensyedriadh.productservice.router;

import io.github.hossensyedriadh.productservice.handler.ProductHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ProductRouter {
    private final ProductHandler productHandler;

    @Autowired
    public ProductRouter(ProductHandler productHandler) {
        this.productHandler = productHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions.route(RequestPredicates.GET(ProductRouterConstants.V1_PRODUCTS_PAGEABLE.getRoute()),
                        this.productHandler::products)
                .andRoute(RequestPredicates.GET(ProductRouterConstants.V1_PRODUCT_BY_ID.getRoute()),
                        this.productHandler::product)
                .andRoute(RequestPredicates.POST(ProductRouterConstants.V1_ADD_PRODUCT.getRoute())
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        this.productHandler::add)
                .andRoute(RequestPredicates.PUT(ProductRouterConstants.V1_MODIFY_PRODUCT.getRoute())
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        this.productHandler::modify)
                .andRoute(RequestPredicates.DELETE(ProductRouterConstants.V1_DELETE_PRODUCT.getRoute()),
                        this.productHandler::delete);
    }
}
