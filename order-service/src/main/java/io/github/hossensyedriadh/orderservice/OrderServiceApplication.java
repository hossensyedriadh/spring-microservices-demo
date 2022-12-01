package io.github.hossensyedriadh.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableEurekaClient
@EnableFeignClients(basePackages = {"io.github.hossensyedriadh.orderservice.proxy"})
@EnableWebFlux
@EnableReactiveMongoRepositories(basePackages = {"io.github.hossensyedriadh.orderservice.repository"})
@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
