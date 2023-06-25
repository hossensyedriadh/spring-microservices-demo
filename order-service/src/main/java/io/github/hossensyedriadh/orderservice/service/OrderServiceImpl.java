package io.github.hossensyedriadh.orderservice.service;

import io.github.hossensyedriadh.orderservice.entity.Item;
import io.github.hossensyedriadh.orderservice.entity.Order;
import io.github.hossensyedriadh.orderservice.enumerator.OrderStatus;
import io.github.hossensyedriadh.orderservice.exception.ResourceException;
import io.github.hossensyedriadh.orderservice.model.Product;
import io.github.hossensyedriadh.orderservice.repository.OrderRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j
@Service
public final class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClient;
    private final ReactiveKafkaProducerTemplate<String, Order> reactiveKafkaProducerTemplate;

    @Value("${kafka.producer.topic.create-order}")
    private String createOrderTopic;

    @Value("${kafka.producer.topic.update-order}")
    private String updateOrderTopic;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, WebClient.Builder webClient,
                            ReactiveKafkaProducerTemplate<String, Order> reactiveKafkaProducerTemplate) {
        this.orderRepository = orderRepository;
        this.webClient = webClient;
        this.reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate;
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
        if (order.getItems().isEmpty()) {
            throw new ResourceException("No Items added in the order", HttpStatus.BAD_REQUEST);
        }

        Flux<Item> items = Flux.fromIterable(order.getItems());
        List<Item> itemList = new ArrayList<>();

        Mono<Boolean> valid = items.flatMap(item -> this.webClient.build().get().uri("http://product-service/products-api/v1/products/{id}", item.getProductRef())
                .retrieve().bodyToMono(Product.class)
                .onErrorResume(WebClientResponseException.BadRequest.class, e -> Mono.error(new ResourceException(e.getMessage(), HttpStatus.BAD_REQUEST)))
                .onErrorResume(WebClientResponseException.ServiceUnavailable.class, e -> Mono.error(new ResourceException("No instance of 'product-service' is available",
                        HttpStatus.SERVICE_UNAVAILABLE)))
                .onErrorResume(WebClientResponseException.InternalServerError.class, e -> Mono.error(new ResourceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)))
                .onErrorResume(WebClientResponseException.Unauthorized.class, e -> Mono.error(new ResourceException(e.getMessage(), HttpStatus.UNAUTHORIZED)))
                .flatMap(product -> {
                    if (product.getStock() >= item.getQuantity()) {
                        item.setId(UUID.randomUUID().toString());
                        itemList.add(item);
                        return Mono.just(item);
                    } else {
                        return Mono.error(new ResourceException("Not enough stock available of product: " + product.getId(), HttpStatus.BAD_REQUEST));
                    }
                })).hasElements();

        return valid.flatMap(v -> {
            order.setItems(itemList);
            order.setCreatedOn(Instant.now(Clock.systemDefaultZone()).getEpochSecond());

            return this.orderRepository.save(order).flatMap(o -> this.reactiveKafkaProducerTemplate.send(this.createOrderTopic, o)
                    .doOnSuccess(result -> log.info("Sent " + o + " | Topic: " + result.recordMetadata().topic() + " | Offset: " + result.recordMetadata().offset()))
                    .then(Mono.just(o)));
        });
    }

    @Override
    public Mono<Order> update(Order order) {
        Mono<Order> orderMono = this.orderRepository.findById(order.getId());
        return orderMono.map(o -> o).flatMap(s -> {
            if (s.getStatus() == OrderStatus.CANCELLED || s.getStatus() == OrderStatus.DELIVERED) {
                return Mono.error(new ResourceException("Cancelled/Delivered orders can not be updated", HttpStatus.BAD_REQUEST));
            }

            s.setStatus(order.getStatus());

            Mono<Order> updatedOrderMono = this.orderRepository.save(s);

            if (order.getStatus() == OrderStatus.CANCELLED) {
                return updatedOrderMono.flatMap(o -> this.reactiveKafkaProducerTemplate.send(this.updateOrderTopic, o)
                        .doOnSuccess(result -> log.info("Sent " + o + " | Topic: " + result.recordMetadata().topic() + " | Offset: " + result.recordMetadata().offset()))
                        .then(Mono.just(o)));
            }

            return updatedOrderMono;
        }).switchIfEmpty(Mono.error(new ResourceException("Order not found with ID: " + order.getId(), HttpStatus.BAD_REQUEST)));
    }
}
