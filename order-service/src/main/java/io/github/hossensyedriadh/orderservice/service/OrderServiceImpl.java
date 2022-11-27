package io.github.hossensyedriadh.orderservice.service;

import io.github.hossensyedriadh.orderservice.entity.Order;
import io.github.hossensyedriadh.orderservice.exception.ResourceException;
import io.github.hossensyedriadh.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.ZonedDateTime;

@Service
public final class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Mono<Page<Order>> orders(Pageable pageable) {
        return this.orderRepository.findAllBy(pageable).switchIfEmpty(Mono.error(new ResourceException(HttpStatus.NO_CONTENT))).collectList()
                .zipWith(this.orderRepository.count())
                .map(o -> new PageImpl<>(o.getT1(), pageable, o.getT2()));
    }

    @Override
    public Mono<Order> order(String id) {
        return this.orderRepository.findById(id);
    }

    @Override
    public Mono<Order> create(Order order) {
        order.setCreatedOn(ZonedDateTime.now(Clock.systemDefaultZone()));
        return this.orderRepository.save(order);
    }

    @Override
    public Mono<Order> update(Order order) {
        Mono<Order> orderMono = this.orderRepository.findById(order.getId());
        return orderMono.map(o -> o).flatMap(s -> {
            s.setStatus(order.getStatus());
            return this.orderRepository.save(s);
        }).switchIfEmpty(Mono.error(new ResourceException("Order not found with ID: " + order.getId(), HttpStatus.BAD_REQUEST)));
    }
}
