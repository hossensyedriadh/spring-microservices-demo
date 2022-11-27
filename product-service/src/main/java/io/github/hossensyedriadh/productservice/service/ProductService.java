package io.github.hossensyedriadh.productservice.service;

import io.github.hossensyedriadh.productservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public sealed interface ProductService permits ProductServiceImpl {
    Mono<Page<Product>> products(Pageable pageable);

    Mono<Product> product(String id);

    Mono<Product> add(Product product);

    Mono<Product> modify(Product product);

    Mono<Void> delete(String id);
}
