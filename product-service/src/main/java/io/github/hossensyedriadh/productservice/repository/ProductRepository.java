package io.github.hossensyedriadh.productservice.repository;

import io.github.hossensyedriadh.productservice.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, String>, ReactiveSortingRepository<Product, String> {
    Flux<Product> findAllBy(Pageable pageable);
}
