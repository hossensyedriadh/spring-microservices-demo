package io.github.hossensyedriadh.productservice.service;

import io.github.hossensyedriadh.productservice.entity.Product;
import io.github.hossensyedriadh.productservice.model.Item;
import io.github.hossensyedriadh.productservice.model.Order;
import io.github.hossensyedriadh.productservice.repository.ProductRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ProductOrderProcessingService implements CommandLineRunner {
    private final Logger logger = Logger.getLogger(ProductOrderProcessingService.class);

    private final ReactiveKafkaConsumerTemplate<String, Order> createOrderKafkaTemplate;
    private final ReactiveKafkaConsumerTemplate<String, Order> updateOrderKafkaTemplate;
    private final ProductRepository productRepository;

    @Autowired
    public ProductOrderProcessingService(@Qualifier("createOrderTemplate") ReactiveKafkaConsumerTemplate<String, Order> createOrderKafkaTemplate,
                                         @Qualifier("updateOrderTemplate") ReactiveKafkaConsumerTemplate<String, Order> updateOrderKafkaTemplate,
                                         ProductRepository productRepository) {
        this.createOrderKafkaTemplate = createOrderKafkaTemplate;
        this.updateOrderKafkaTemplate = updateOrderKafkaTemplate;
        this.productRepository = productRepository;
    }

    private Flux<Order> listenCreateOrderTopic() {
        return this.createOrderKafkaTemplate.receiveAutoAck().map(ConsumerRecord::value).doOnNext(order -> logger.info("New order in queue: " + order))
                .doOnError(throwable -> logger.error("Consumption error: " + throwable.getMessage()));
    }

    private Flux<Order> listenUpdateOrderTopic() {
        return this.updateOrderKafkaTemplate.receiveAutoAck().map(ConsumerRecord::value).doOnNext(order -> logger.info("Order update in queue: " +order))
                .doOnError(throwable -> logger.error("Consumption error: " +throwable.getMessage()));
    }

    @Override
    public void run(String... args) {
        this.listenCreateOrderTopic().flatMap(order -> {
            Flux<Product> productFlux = this.productRepository.findAllById(Flux.fromStream(order.getItems().stream().map(Item::getProductRef)));
            Flux<Item> itemFlux = Flux.fromIterable(order.getItems());

            return productFlux.flatMap(product -> itemFlux.flatMap(item -> {
                product.setStock(product.getStock() - item.getQuantity());
                return this.productRepository.save(product);
            }));
        }).subscribe();

        this.listenUpdateOrderTopic().flatMap(order -> {
            Flux<Product> productFlux = this.productRepository.findAllById(Flux.fromStream(order.getItems().stream().map(Item::getProductRef)));
            Flux<Item> itemFlux = Flux.fromIterable(order.getItems());

            return productFlux.flatMap(product -> itemFlux.flatMap(item -> {
                product.setStock(product.getStock() + item.getQuantity());
                return this.productRepository.save(product);
            }));
        }).subscribe();
    }
}
