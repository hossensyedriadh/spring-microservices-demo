package io.github.hossensyedriadh.userservice.repository;

import io.github.hossensyedriadh.userservice.entity.UserAccount;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.AutoConfigureDataR2dbc;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataR2dbcTest
@AutoConfigureDataR2dbc
@DirtiesContext
public class UserRepositoryTests {
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Test
    public void get_users() {
        Flux<UserAccount> userAccountFlux = this.userAccountRepository.findAll().doOnNext(System.out::println);

        StepVerifier.create(userAccountFlux).expectSubscription()
                .expectNextCount(0).verifyComplete();
    }

    @Test
    public void get_user() {
        Mono<UserAccount> userAccountMono = this.userAccountRepository.findById("test").doOnNext(System.out::println);

        StepVerifier.create(userAccountMono).expectSubscription()
                .verifyComplete();
    }
}
