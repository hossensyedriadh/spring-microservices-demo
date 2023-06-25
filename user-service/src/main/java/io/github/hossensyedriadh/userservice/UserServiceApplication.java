package io.github.hossensyedriadh.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableDiscoveryClient
@EnableR2dbcRepositories(basePackages = {"io.github.hossensyedriadh.userservice.repository"})
@EnableWebFlux
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
