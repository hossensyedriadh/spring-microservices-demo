package io.github.hossensyedriadh.openservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableR2dbcAuditing
@EnableDiscoveryClient
@EnableWebFlux
@EnableR2dbcRepositories(basePackages = {"io.github.hossensyedriadh.openservice.repository.r2dbc"})
@SpringBootApplication
public class OpenServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenServiceApplication.class, args);
    }

}
