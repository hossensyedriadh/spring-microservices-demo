package io.github.hossensyedriadh.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableEurekaClient
@EnableR2dbcRepositories(basePackages = {"io.github.hossensyedriadh.authservice.repository.r2dbc"})
@EnableReactiveMongoRepositories(basePackages = {"io.github.hossensyedriadh.authservice.repository.mongo"})
@EnableWebFlux
@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
