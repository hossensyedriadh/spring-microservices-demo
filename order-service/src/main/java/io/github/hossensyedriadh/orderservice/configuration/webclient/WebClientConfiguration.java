package io.github.hossensyedriadh.orderservice.configuration.webclient;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@LoadBalancerClients(value = {
        @LoadBalancerClient(name = "product-service", configuration = LoadBalancerClientConfiguration.class),
        @LoadBalancerClient(name = "user-service", configuration = LoadBalancerClientConfiguration.class)
})
public class WebClientConfiguration {
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
