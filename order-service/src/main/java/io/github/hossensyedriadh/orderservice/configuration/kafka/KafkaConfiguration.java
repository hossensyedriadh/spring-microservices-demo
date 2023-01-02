package io.github.hossensyedriadh.orderservice.configuration.kafka;

import io.github.hossensyedriadh.orderservice.entity.Order;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

@EnableKafka
@Configuration
public class KafkaConfiguration {
    @Bean
    public ReactiveKafkaProducerTemplate<String, Order> reactiveKafkaProducerTemplate(KafkaProperties kafkaProperties) {
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(kafkaProperties.buildProducerProperties()));
    }
}
