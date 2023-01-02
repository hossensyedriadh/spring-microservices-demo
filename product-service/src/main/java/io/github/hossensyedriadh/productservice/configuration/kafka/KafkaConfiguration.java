package io.github.hossensyedriadh.productservice.configuration.kafka;

import io.github.hossensyedriadh.productservice.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;

@EnableKafka
@Configuration
public class KafkaConfiguration {
    @Value("${kafka.consumer.topic.create-order}")
    private String createOrderTopic;

    @Value("${kafka.consumer.topic.update-order}")
    private String updateOrderTopic;

    @Bean
    public ReceiverOptions<String, Order> receiverOptions(KafkaProperties kafkaProperties) {
        ReceiverOptions<String, Order> receiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
        return receiverOptions.subscription(List.of(this.createOrderTopic, this.updateOrderTopic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, Order> reactiveKafkaConsumerTemplate(ReceiverOptions<String, Order> receiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }
}
