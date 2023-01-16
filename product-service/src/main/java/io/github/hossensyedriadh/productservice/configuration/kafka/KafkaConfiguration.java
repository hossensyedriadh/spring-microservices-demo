package io.github.hossensyedriadh.productservice.configuration.kafka;

import io.github.hossensyedriadh.productservice.model.Order;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.List;

@EnableKafka
@Configuration
public class KafkaConfiguration {
    @Value("${kafka.consumer.topic.create-order}")
    private String createOrderTopic;

    @Value("${kafka.consumer.topic.update-order}")
    private String updateOrderTopic;

    @Bean("createOrderOptions")
    public ReceiverOptions<String, Order> receiverOptionsCreateOrderTopic(KafkaProperties kafkaProperties) {
        ReceiverOptions<String, Order> receiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
        return receiverOptions.subscription(Collections.singleton(this.createOrderTopic));
    }

    @Bean("createOrderTemplate")
    public ReactiveKafkaConsumerTemplate<String, Order> reactiveKafkaConsumerTemplateCreateOrderTopic(@Qualifier("createOrderOptions")
                                                                                                          ReceiverOptions<String, Order> receiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }

    @Bean("updateOrderOptions")
    public ReceiverOptions<String, Order> receiverOptionsUpdateOrderTopic(KafkaProperties kafkaProperties) {
        ReceiverOptions<String, Order> receiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
        return receiverOptions.subscription(Collections.singleton(this.updateOrderTopic));
    }

    @Bean("updateOrderTemplate")
    public ReactiveKafkaConsumerTemplate<String, Order> reactiveKafkaConsumerTemplateUpdateOrderTopic(@Qualifier("updateOrderOptions")
                                                                                                      ReceiverOptions<String, Order> receiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }
}
