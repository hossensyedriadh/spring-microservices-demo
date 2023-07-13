package io.github.hossensyedriadh.mailservice.configuration.kafka;

import io.github.hossensyedriadh.mailservice.model.MailModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;

@EnableKafka
@Configuration
public class KafkaConfiguration {

    @Value("${kafka.consumer.topic.mail}")
    private String mailTopic;

    @Bean
    public ReceiverOptions<String, MailModel> receiverOptions(KafkaProperties kafkaProperties) {
        ReceiverOptions<String, MailModel> receiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
        return receiverOptions.subscription(Collections.singleton(this.mailTopic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, MailModel> stringStringReactiveKafkaConsumerTemplate(ReceiverOptions<String, MailModel> receiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }
}
