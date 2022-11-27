package io.github.hossensyedriadh.productservice.service;

import io.github.hossensyedriadh.productservice.entity.Product;
import io.github.hossensyedriadh.productservice.exception.ResourceException;
import io.github.hossensyedriadh.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public final class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Mono<Page<Product>> products(Pageable pageable) {
        return this.productRepository.findAllBy(pageable).switchIfEmpty(Mono.error(new ResourceException(HttpStatus.NO_CONTENT)))
                .collectList().zipWith(this.productRepository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    @Override
    public Mono<Product> product(String id) {
        return this.productRepository.findById(id);
    }

    @Override
    public Mono<Product> add(Product product) {
        return this.productRepository.save(product);
    }

    @Override
    public Mono<Product> modify(Product product) {
        return this.productRepository.findById(product.getId()).map(p -> product).flatMap(this.productRepository::save)
                .switchIfEmpty(Mono.error(new ResourceException("Product not found with ID: " + product.getId(), HttpStatus.BAD_REQUEST)));
    }

    @Override
    public Mono<Void> delete(String id) {
        return this.productRepository.findById(id).switchIfEmpty(Mono.error(new ResourceException("Product not found with ID: " + id, HttpStatus.BAD_REQUEST)))
                .flatMap(this.productRepository::delete);
    }
}
