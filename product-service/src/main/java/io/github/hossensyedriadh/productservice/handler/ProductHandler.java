package io.github.hossensyedriadh.productservice.handler;

import io.github.hossensyedriadh.productservice.entity.Product;
import io.github.hossensyedriadh.productservice.exception.ResourceException;
import io.github.hossensyedriadh.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductHandler {
    private final ProductService productService;

    @Autowired
    public ProductHandler(ProductService productService) {
        this.productService = productService;
    }

    private int defaultSize;

    @Value("${spring.data.rest.default-page-size}")
    public void setDefaultSize(int defaultSize) {
        this.defaultSize = defaultSize;
    }

    public Mono<ServerResponse> products(ServerRequest serverRequest) {
        int size = serverRequest.queryParam("size").isPresent() ?
                Integer.parseInt(serverRequest.queryParam("size").get()) : defaultSize;

        int page = serverRequest.queryParam("page").isPresent() ?
                Integer.parseInt(serverRequest.queryParam("page").get()) : 0;

        List<Sort.Order> sortOrders = new ArrayList<>();
        if (serverRequest.queryParam("sort").isPresent()) {
            String sortString = serverRequest.queryParam("sort").get();
            if (sortString.contains(",")) {
                String[] sort = serverRequest.queryParam("sort").get().split(",");
                sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1])
                        .orElse(Sort.Direction.ASC), sort[0]));
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));
        Mono<Page<Product>> products = this.productService.products(pageable);
        return products.hasElement().flatMap(b -> b ? ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(products, Product.class) : ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> product(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Product> productMono = this.productService.product(id);

        return productMono.hasElement().flatMap(b -> b ? ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(productMono, Product.class) : ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> add(ServerRequest serverRequest) {
        Mono<Product> productMono = serverRequest.bodyToMono(Product.class);

        return productMono.flatMap(product -> ServerResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
                .body(this.productService.add(product)
                        .onErrorResume(e -> Mono.error(new ResourceException(e.getMessage(),
                                HttpStatus.BAD_REQUEST, serverRequest))), Product.class));
    }

    public Mono<ServerResponse> modify(ServerRequest serverRequest) {
        Mono<Product> productMono = serverRequest.bodyToMono(Product.class);

        return productMono.flatMap(product -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(this.productService.modify(product)
                .onErrorResume(e -> Mono.error(new ResourceException(e.getMessage(),
                        HttpStatus.BAD_REQUEST, serverRequest))), Product.class));
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Void> voidMono = this.productService.delete(id);

        return voidMono.then(ServerResponse.noContent().build()).onErrorResume(e ->
                Mono.error(new ResourceException(e.getMessage(), HttpStatus.BAD_REQUEST, serverRequest))
        );
    }
}
