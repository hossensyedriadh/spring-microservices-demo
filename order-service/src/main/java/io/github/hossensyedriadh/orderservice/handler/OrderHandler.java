package io.github.hossensyedriadh.orderservice.handler;

import io.github.hossensyedriadh.orderservice.entity.Order;
import io.github.hossensyedriadh.orderservice.exception.ResourceException;
import io.github.hossensyedriadh.orderservice.service.OrderService;
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
public class OrderHandler {
    private final OrderService orderService;

    @Autowired
    public OrderHandler(OrderService orderService) {
        this.orderService = orderService;
    }

    private int defaultSize;

    @Value("${spring.data.rest.default-page-size}")
    public void setDefaultSize(int defaultSize) {
        this.defaultSize = defaultSize;
    }

    public Mono<ServerResponse> orders(ServerRequest serverRequest) {
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
        Mono<Page<Order>> orders = this.orderService.orders(pageable);
        return orders.hasElement().flatMap(b -> b ? ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(orders, Order.class) : ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> order(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Order> orderMono = this.orderService.order(id);

        return orderMono.hasElement().flatMap(b -> b ? ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(orderMono, Order.class) : ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        Mono<Order> orderMono = serverRequest.bodyToMono(Order.class);

        return orderMono.flatMap(order -> ServerResponse.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON).body(this.orderService.create(order).onErrorResume(e -> Mono.error(new ResourceException(e.getMessage(),
                        HttpStatus.BAD_REQUEST, serverRequest))), Order.class));
    }

    public Mono<ServerResponse> modify(ServerRequest serverRequest) {
        Mono<Order> orderMono = serverRequest.bodyToMono(Order.class);

        return orderMono.flatMap(order -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(this.orderService.update(order)
                .onErrorResume(e -> Mono.error(new ResourceException(e.getMessage(),
                        HttpStatus.BAD_REQUEST, serverRequest))), Order.class));
    }
}
