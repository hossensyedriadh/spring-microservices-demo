package io.github.hossensyedriadh.orderservice.repository;

import io.github.hossensyedriadh.orderservice.OrderServiceApplication;
import io.github.hossensyedriadh.orderservice.configuration.datasource.MongoConfiguration;
import io.github.hossensyedriadh.orderservice.entity.Item;
import io.github.hossensyedriadh.orderservice.entity.Order;
import io.github.hossensyedriadh.orderservice.enumerator.OrderStatus;
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

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataMongoTest
@AutoConfigureDataMongo
@ContextConfiguration(classes = {OrderServiceApplication.class, MongoConfiguration.class, OrderRepository.class})
@DirtiesContext
public class OrderRepositoryTests {
    @Autowired
    private OrderRepository orderRepository;

    private final List<Order> orders = Arrays.asList(new Order(UUID.randomUUID().toString(),
            List.of(new Item(UUID.randomUUID().toString(), 1, 5000, "product_1")), 5000,
            OrderStatus.ACTIVE, ZonedDateTime.now(), "test"), new Order(UUID.randomUUID().toString(),
            List.of(new Item(UUID.randomUUID().toString(), 3, 3000, "product_2")), 9000,
            OrderStatus.ACTIVE, ZonedDateTime.now(), "test"));

    @Before
    public void seed() {
        this.orderRepository.deleteAll().thenMany(Flux.fromIterable(this.orders))
                .flatMap(this.orderRepository::save).doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    public void get_all_orders() {
        Flux<Order> orderFlux = this.orderRepository.findAll().doOnNext(System.out::println);

        StepVerifier.create(orderFlux).expectSubscription()
                .expectNextCount(this.orders.size()).verifyComplete();
    }

    @Test
    public void get_order() {
        Mono<Order> orderMono = this.orderRepository.findById(this.orders.get(0).getId()).doOnNext(System.out::println);

        StepVerifier.create(orderMono).expectSubscription().expectNextCount(1).verifyComplete();
    }

    @Test
    public void save_order() {
        String id = UUID.randomUUID().toString();
        Order order = new Order(id,
                List.of(new Item(UUID.randomUUID().toString(), 2, 2000, "product_2")), 4000,
                OrderStatus.ACTIVE, ZonedDateTime.now(), "test");

        Mono<Order> orderMono = this.orderRepository.save(order).doOnNext(System.out::println);

        StepVerifier.create(orderMono).expectSubscription()
                .expectNext(order).verifyComplete();
    }

    @Test
    public void update_order() {
        this.orders.get(1).setStatus(OrderStatus.DELIVERED);

        Mono<Order> orderMono = this.orderRepository.save(this.orders.get(1)).doOnNext(System.out::println);

        StepVerifier.create(orderMono).expectSubscription()
                .expectNext(this.orders.get(1)).verifyComplete();
    }
}