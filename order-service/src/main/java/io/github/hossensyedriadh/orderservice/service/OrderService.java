package io.github.hossensyedriadh.orderservice.service;

import io.github.hossensyedriadh.orderservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public sealed interface OrderService permits OrderServiceImpl {
    Mono<Page<Order>> orders(Pageable pageable);

    Mono<Order> order(String id);

    Mono<Order> create(Order order);

    Mono<Order> update(Order order);
}
