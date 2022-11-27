package io.github.hossensyedriadh.orderservice.repository;

import io.github.hossensyedriadh.orderservice.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OrderRepository extends ReactiveMongoRepository<Order, String>, ReactiveSortingRepository<Order, String> {
    Flux<Order> findAllBy(Pageable pageable);
}
