package io.github.hossensyedriadh.productservice.repository;

import io.github.hossensyedriadh.productservice.ProductServiceApplication;
import io.github.hossensyedriadh.productservice.entity.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataMongoTest
@AutoConfigureDataMongo
@ContextConfiguration(classes = {ProductServiceApplication.class, ProductRepository.class})
@DirtiesContext
public class ProductRepositoryTests {
    @Autowired
    private ProductRepository productRepository;

    private final List<Product> products = Arrays.asList(new Product(UUID.randomUUID().toString(), "Product 1", "Category 1",
            "specs..", 1, 500), new Product(UUID.randomUUID().toString(), "Product 2", "Category 2",
            "some specs..", 5, 10000), new Product(UUID.randomUUID().toString(), "Product 3", "Category 3",
            "specifications..", 10, 100000));

    @Before
    public void seed() {
        this.productRepository.deleteAll().thenMany(Flux.fromIterable(this.products))
                .flatMap(this.productRepository::save).doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    public void get_all_products() {
        Flux<Product> productFlux = this.productRepository.findAll().doOnNext(System.out::println);

        StepVerifier.create(productFlux).expectSubscription()
                .expectNextCount(this.products.size()).verifyComplete();
    }

    @Test
    public void get_product() {
        Mono<Product> productMono = this.productRepository.findById(this.products.get(0).getId())
                .doOnNext(System.out::println);

        StepVerifier.create(productMono).expectSubscription()
                .expectNext(this.products.get(0)).verifyComplete();
    }

    @Test
    public void save_product() {
        String id = UUID.randomUUID().toString();

        Product product = new Product(id, "Dummy", "Dummy", ".....", 1, 10);

        Mono<Product> productMono = this.productRepository.save(product).doOnNext(System.out::println);

        StepVerifier.create(productMono).expectSubscription()
                .expectNext(product).verifyComplete();
    }

    @Test
    public void update_product() {
        this.products.get(1).setPrice(11000);

        Mono<Product> productMono = this.productRepository.save(this.products.get(1)).doOnNext(System.out::println);

        StepVerifier.create(productMono).expectSubscription()
                .expectNext(this.products.get(1)).verifyComplete();
    }

    @Test
    public void delete_product() {
        Mono<Void> voidMono = this.productRepository.delete(this.products.get(2)).doOnNext(System.out::println);

        StepVerifier.create(voidMono).expectSubscription()
                .verifyComplete();
    }
}
