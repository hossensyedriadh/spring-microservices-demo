package io.github.hossensyedriadh.userservice.handler;

import io.github.hossensyedriadh.userservice.UserServiceApplication;
import io.github.hossensyedriadh.userservice.entity.UserAccount;
import io.github.hossensyedriadh.userservice.enumerator.Authority;
import io.github.hossensyedriadh.userservice.router.UserRouter;
import io.github.hossensyedriadh.userservice.router.UserRouterConstants;
import io.github.hossensyedriadh.userservice.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient
@SpringBootTest(classes = {UserServiceApplication.class, UserHandler.class, UserRouter.class})
public class UserHandlerTests {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserService userService;

    @Test
    public void when_get_users_return_2xx() {
        this.webTestClient.get().uri(UserRouterConstants.V1_USERS_PAGEABLE.getRoute())
                .exchange().expectStatus().is2xxSuccessful().returnResult(UserAccount.class)
                .consumeWith(System.out::println);
    }

    @Test
    public void when_get_user_return_2xx() {
        this.webTestClient.get().uri(UserRouterConstants.V1_USERS_BY_USERNAME.getRoute(), "test")
                .exchange().expectStatus().is2xxSuccessful().returnResult(UserAccount.class)
                .consumeWith(System.out::println);
    }

    @Test
    public void when_put_user_return_400() {
        this.webTestClient.put().uri(UserRouterConstants.V1_USER_UPDATE.getRoute())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new UserAccount("test", "test", Authority.ROLE_CUSTOMER, "Test", "User",
                        "test@email.test", "Localhost")), UserAccount.class)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isBadRequest().returnResult(UserAccount.class)
                .consumeWith(System.out::println);
    }

    @Test
    public void when_delete_user_return_400() {
        this.webTestClient.delete().uri(UserRouterConstants.V1_USER_DELETE.getRoute(), "test")
                .exchange().expectStatus().isBadRequest().returnResult(UserAccount.class)
                .consumeWith(System.out::println);
    }
}
