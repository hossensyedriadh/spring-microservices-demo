package io.github.hossensyedriadh.productservice.service;

import io.github.hossensyedriadh.productservice.entity.Product;
import io.github.hossensyedriadh.productservice.model.Item;
import io.github.hossensyedriadh.productservice.model.Order;
import io.github.hossensyedriadh.productservice.repository.ProductRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ProductOrderProcessingService implements CommandLineRunner {
    private final Logger logger = Logger.getLogger(ProductOrderProcessingService.class);

    private final ReactiveKafkaConsumerTemplate<String, Order> reactiveKafkaConsumerTemplate;
    private final ProductRepository productRepository;

    @Autowired
    public ProductOrderProcessingService(ReactiveKafkaConsumerTemplate<String, Order> reactiveKafkaConsumerTemplate,
                                         ProductRepository productRepository) {
        this.reactiveKafkaConsumerTemplate = reactiveKafkaConsumerTemplate;
        this.productRepository = productRepository;
    }

    private Flux<Order> listen() {
        return this.reactiveKafkaConsumerTemplate.receiveAutoAck().map(ConsumerRecord::value).doOnNext(order -> logger.info("New order in queue: " + order))
                .doOnError(throwable -> logger.error("Consumption error: " + throwable.getMessage()));
    }

    @Override
    public void run(String... args) {
        this.listen().flatMap(order -> {
            Flux<Product> productFlux = this.productRepository.findAllById(Flux.fromStream(order.getItems().stream().map(Item::getProductRef)));
            Flux<Item> itemFlux = Flux.fromIterable(order.getItems());

            return productFlux.flatMap(product -> itemFlux.flatMap(item -> {
                product.setStock(product.getStock() - item.getQuantity());
                return this.productRepository.save(product);
            }));
        }).subscribe();
    }
}
